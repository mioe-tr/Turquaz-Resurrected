#!/usr/bin/env python3
"""
2005 demo verisini modern şemaya taşıyan Flyway migration üreticisi.

Girdi:
- Legacy PG DB (`turquaz_legacy`) — 2005 pg_dump'tan restore edilmiş.
- Modern V1 şema (sütun listesi/FK grafı).

Çıktı:
- `persistence/src/main/resources/db/migration/V2__seed.sql`
  Tüm 2005 demo verisini, eski integer id'leri deterministik UUIDv7'ye
  eşleyerek, default şirkete bağlayarak, dependency sırasında INSERT eder.

Notlar:
- session_replication_role = replica ile FK kısıtları seed süresince devre
  dışı (self-referans + 4000+ FK için tek seferlik düzgün çözüm).
- UUIDv7'ler deterministik (seed başlangıç zamanı + tablo+id hash) ki
  migration tekrar üretildiğinde aynı id'ler oluşsun.
"""
from __future__ import annotations
import os
import re
import sys
import uuid
import hashlib
from collections import defaultdict, deque
from pathlib import Path

import psycopg2
import psycopg2.extras

ROOT = Path("/home/user/Turquaz-Resurrected")
V1 = ROOT / "persistence/src/main/resources/db/migration/V1__sema.sql"
OUT = ROOT / "persistence/src/main/resources/db/migration/V2__seed.sql"

LEGACY_DSN = "dbname=turquaz_legacy host=127.0.0.1 user=postgres"

# Migration kararlılığı için sabit zaman ekseni (UUIDv7 epoch ms).
# 2026-05-28T00:00:00Z = 1779984000000 ms
SEED_EPOCH_MS = 1779984000000

GLOBAL_TABLES = {
    "turq_companies", "turq_modules", "turq_module_components", "turq_engine_menu",
}


def deterministic_uuid7(table: str, old_id: int) -> uuid.UUID:
    """Tablo+eski-id'den deterministik UUIDv7 üret."""
    h = hashlib.sha256(f"{table}#{old_id}".encode()).digest()
    # İlk 48 bit: SEED_EPOCH_MS (zaman-sıralı tarafı koru)
    ts = SEED_EPOCH_MS
    msb = (
        ((ts & 0xFFFFFFFFFFFF) << 16)
        | (0x7 << 12)
        | ((h[0] & 0x0F) << 8)
        | (h[1] & 0xFF)
    )
    lsb = (0x2 << 62)
    lsb |= (h[2] & 0x3F) << 56
    for i in range(3, 10):
        lsb |= h[i] << ((9 - i) * 8)
    # 64-bit kırpma (Python int sınırsız)
    msb &= (1 << 64) - 1
    lsb &= (1 << 64) - 1
    return uuid.UUID(int=(msb << 64) | lsb)


# --- V1'i ayrıştır: sütun listesi ve FK grafı --------------------------------

TABLE_RE = re.compile(r"^CREATE TABLE (\S+) \(\s*$")
COL_RE = re.compile(r"^\s+([a-z_][a-z0-9_]*)\s+(.+?)(\s+NOT NULL)?,?\s*$", re.IGNORECASE)
FK_RE = re.compile(
    r"ALTER TABLE (\S+) ADD CONSTRAINT \S+ FOREIGN KEY \(([^)]+)\) REFERENCES (\S+) \(",
    re.IGNORECASE,
)


def parse_v1():
    tables: dict[str, list[tuple[str, str, bool]]] = {}
    fks: list[tuple[str, str, str]] = []  # (src, col, tgt)
    cur = None
    cols: list = []
    with V1.open() as f:
        for line in f:
            line = line.rstrip("\n")
            m = TABLE_RE.match(line)
            if m:
                cur = m.group(1)
                cols = []
                continue
            if cur and line.startswith(");"):
                tables[cur] = cols
                cur = None
                continue
            if cur:
                mc = COL_RE.match(line)
                if mc:
                    nn = bool(re.search(r"\bNOT NULL\b", line, re.IGNORECASE))
                    sql_type = re.sub(r"\s*NOT NULL\s*$", "", mc.group(2), flags=re.IGNORECASE).strip().rstrip(",")
                    cols.append((mc.group(1), sql_type, nn))
            mf = FK_RE.search(line)
            if mf and cur is None:
                fks.append((mf.group(1), mf.group(2), mf.group(3)))
    return tables, fks


def topo_sort(tables: set[str], fks: list[tuple[str, str, str]]) -> list[str]:
    """Bağımlılık sırasında tablo listesi (referans verilenler önce). Self-FK
    ihmal edilir (self-loop). company_id FK'leri zaten companies'i ilk hale alır."""
    deps = defaultdict(set)  # src bağımlı tgt'ye
    rev = defaultdict(set)
    nodes = set(tables)
    for src, _col, tgt in fks:
        if src == tgt:
            continue
        if src not in nodes or tgt not in nodes:
            continue
        deps[src].add(tgt)
        rev[tgt].add(src)
    in_deg = {t: len(deps[t]) for t in nodes}
    q = deque(sorted(t for t, d in in_deg.items() if d == 0))
    out = []
    while q:
        t = q.popleft()
        out.append(t)
        for s in sorted(rev[t]):
            in_deg[s] -= 1
            if in_deg[s] == 0:
                q.append(s)
    if len(out) != len(nodes):
        missing = nodes - set(out)
        raise RuntimeError(f"Topo sort çıkmaza girdi (döngü?): {missing}")
    return out


# --- Değer formatlama -------------------------------------------------------

def sql_literal(v, col, sql_type, id_map, fk_target, source_table):
    if v is None:
        return "NULL"
    s = sql_type.lower()
    if s == "uuid":
        tgt = fk_target
        # hbm m2o'da olmayan tek bilinen self-FK: turq_engine_menu.parent_id
        if tgt is None and col == "parent_id":
            tgt = source_table
        if isinstance(v, int):
            if tgt is None:
                return "NULL"
            new = id_map.get(tgt, {}).get(v)
            return f"'{new}'" if new is not None else "NULL"
        return f"'{v}'"
    if s in ("integer", "smallint", "bigint", "int2", "int4", "int8"):
        return str(int(v))
    if s.startswith("numeric"):
        return str(v)
    if s.startswith("boolean"):
        return "TRUE" if v else "FALSE"
    if s.startswith("timestamp") or s.startswith("date"):
        # psycopg2 datetime/date verir
        return f"'{v.isoformat()}'"
    # varchar / text
    esc = str(v).replace("'", "''")
    return f"'{esc}'"


# --- Ana akış ---------------------------------------------------------------

def main() -> int:
    schema, fks = parse_v1()

    # tablo → {col: target_table} (FK haritası)
    fk_map: dict[str, dict[str, str]] = defaultdict(dict)
    for src, col, tgt in fks:
        fk_map[src][col] = tgt

    order = topo_sort(set(schema.keys()), fks)
    # Topo: bağımsız (referans verilenler) önce; companies, sequences, vs. başta.

    conn = psycopg2.connect(LEGACY_DSN)
    conn.set_session(readonly=True, autocommit=True)

    # Önce id_map'i kur: her tablodaki her satıra UUIDv7 ata.
    id_map: dict[str, dict[int, uuid.UUID]] = defaultdict(dict)
    row_cache: dict[str, list[dict]] = {}
    with conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor) as cur:
        for t in schema:
            cur.execute(f"SELECT to_regclass('public.{t}') IS NOT NULL")
            if not cur.fetchone()["?column?"]:
                row_cache[t] = []
                continue
            cur.execute(f"SELECT * FROM {t}")
            rows = cur.fetchall()
            row_cache[t] = rows
            for r in rows:
                if "id" in r and r["id"] is not None:
                    id_map[t][int(r["id"])] = deterministic_uuid7(t, int(r["id"]))

    # Default şirket: turq_companies varsa ilk satır; yoksa yeni UUID üret.
    companies = row_cache.get("turq_companies", [])
    if companies:
        default_company = id_map["turq_companies"][int(companies[0]["id"])]
    else:
        default_company = deterministic_uuid7("turq_companies", 0)

    # Çıktıyı yaz
    out_lines: list[str] = [
        "-- Turquaz Resurrected — V2: 2005 demo verisini taşıyan seed",
        "-- Üretildi: tools/gen_seed.py (deterministik).",
        "-- FK kısıtları seed süresince devre dışı (session_replication_role = replica).",
        "",
        "BEGIN;",
        "SET LOCAL session_replication_role = 'replica';",
        "",
    ]

    # Eğer companies boşsa, default bir şirket oluştur (her ortamda seed
    # çalışacağı için pratik bir kayıt olmalı).
    if not companies:
        out_lines += [
            "-- Default şirket (legacy dump boştu)",
            f"INSERT INTO turq_companies (id, company_name, company_address, company_telephone, company_fax, created_by, creation_date, updated_by, update_date) "
            f"VALUES ('{default_company}', 'Turquaz Demo', '-', '-', '-', 'seed', NOW(), 'seed', NOW());",
            "",
        ]

    total_rows = 0
    for t in order:
        rows = row_cache.get(t, [])
        if not rows:
            continue
        cols_def = schema[t]
        col_names = [c[0] for c in cols_def if c[0] != "company_id"]
        if t not in GLOBAL_TABLES:
            col_names.append("company_id")
        out_lines.append(f"-- {t}: {len(rows)} satır")
        for r in rows:
            values: list[str] = []
            for c in col_names:
                if c == "company_id":
                    values.append(f"'{default_company}'")
                    continue
                # eski satırda c sütunu olmayabilir (yeni eklenenler) — NULL
                col_info = next((ci for ci in cols_def if ci[0] == c), None)
                sql_type = col_info[1] if col_info else "text"
                fk_target = fk_map[t].get(c)
                if c == "id":
                    values.append(f"'{id_map[t][int(r['id'])]}'")
                elif c in r:
                    values.append(sql_literal(r[c], c, sql_type, id_map, fk_target, t))
                else:
                    values.append("NULL")
            cols_csv = ", ".join(col_names)
            vals_csv = ", ".join(values)
            out_lines.append(f"INSERT INTO {t} ({cols_csv}) VALUES ({vals_csv});")
            total_rows += 1
        out_lines.append("")

    out_lines.append("COMMIT;")
    OUT.write_text("\n".join(out_lines) + "\n")
    print(f"Yazıldı: {OUT}")
    print(f"  Toplam INSERT: {total_rows}")
    print(f"  Default şirket id: {default_company}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
