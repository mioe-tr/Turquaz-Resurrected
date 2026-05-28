#!/usr/bin/env python3
"""
Turquaz Resurrected - Faz 1 şema üreticisi.

2005 pg_dump DDL'ini (`/tmp/sch2.sql`) ve 2018 hbm mapping'lerini girdi alır;
modernleştirilmiş PostgreSQL şemasını (V1) üretir.

Dönüşüm kuralları (ADR-0003):
- id integer       → id uuid (UUIDv7, uygulama tarafı üretim) PRIMARY KEY
- *_id integer ve bilinen FK adları → uuid
- numeric (para)   → numeric(19,4)
- numeric (miktar/oran) → numeric(19,6)
- date             → timestamptz
- character varying(n) → varchar(n); character varying → text
- company_id uuid NOT NULL eklenir (turq_companies + sistem tablolar hariç)
- FK kısıtları hbm many-to-one'lardan türetilir
"""
from __future__ import annotations
import os
import re
import sys
from collections import OrderedDict
from pathlib import Path

LEGACY = Path("/home/user/Turquaz-Resurrected/legacy")
DUMP = Path("/tmp/sch2.sql")
OUT = Path("/home/user/Turquaz-Resurrected/persistence/src/main/resources/db/migration/V1__sema.sql")

# --- Sütun sınıflandırma ---------------------------------------------------

# Miktar / oran sütunları (NUMERIC(19,6)). Geri kalan numeric → para (19,4).
QUANTITY_COLS = {
    "amount_in", "amount_out",        # stok miktarı (giriş/çıkış)
    "card_units_factor",              # birim çevrim katsayısı
    "tradebill_amount",               # irsaliye miktarı
    "exchange_ratio",                 # döviz kuru oranı
    "vat_rate", "vat_special_rate",
    "discount_rate", "cards_discount_rate",
}

# Şirket bazlı multi-tenant kapsam DIŞINDA kalan tablolar (global).
GLOBAL_TABLES = {
    "turq_companies",       # kiracının kendisi
    "turq_modules",         # uygulama modül kataloğu
    "turq_module_components",
    "turq_engine_menu",     # uygulama menü yapısı
}

# FK kümesi hbm many-to-one'lardan run-time'da doldurulur:
# (table, column) -> True. main() bunu doldurur ve map_type'a aktarır.
HBM_FK_COLS: set[tuple[str, str]] = set()


def is_fk_col(table: str, name: str) -> bool:
    # 1) hbm'de açıkça many-to-one ile eşlenen sütun
    if (table, name) in HBM_FK_COLS:
        return True
    # 2) konvansiyon: _id ile biten integer sütunlar (m2o tarafında atlanmış olabilir)
    if name.endswith("_id"):
        return True
    return False


def map_type(table: str, col: str, sql_type: str) -> str:
    s = sql_type.strip().lower()
    if col == "id":
        return "uuid"
    if s in ("integer", "smallint", "bigint", "int2", "int4", "int8"):
        if is_fk_col(table, col):
            return "uuid"
        return s  # gerçek integer (sayaç, seviye, min/max, vb.)
    if s.startswith("numeric"):
        return "numeric(19,6)" if col in QUANTITY_COLS else "numeric(19,4)"
    if s.startswith("date"):
        return "timestamptz"
    if s.startswith("character varying"):
        m = re.search(r"\((\d+)\)", sql_type)
        return f"varchar({m.group(1)})" if m else "text"
    if s.startswith("boolean"):
        return "boolean"
    return sql_type  # son çare


# --- Dump parse ------------------------------------------------------------

COL_RE = re.compile(
    r"^\s*([a-z_][a-z0-9_]*)\s+(.*?)(\s+NOT NULL)?,?\s*$",
    re.IGNORECASE,
)


def parse_dump(path: Path) -> "OrderedDict[str, list[tuple[str,str,bool]]]":
    tables: OrderedDict[str, list[tuple[str, str, bool]]] = OrderedDict()
    cur = None
    cols: list[tuple[str, str, bool]] = []
    with path.open() as f:
        for line in f:
            line = line.rstrip("\n")
            m = re.match(r"^CREATE TABLE (\S+) \(", line)
            if m:
                cur = m.group(1)
                cols = []
                continue
            if cur and line.startswith(");"):
                tables[cur] = cols
                cur = None
                continue
            if cur:
                # her sütun satırı: name type [NOT NULL][,]
                raw = line.rstrip(",").strip()
                if not raw:
                    continue
                # type kısmı NOT NULL kelimesini içerebilir
                parts = raw.split(None, 1)
                if len(parts) != 2:
                    continue
                name, rest = parts
                not_null = bool(re.search(r"\bNOT NULL\b", rest, re.IGNORECASE))
                sql_type = re.sub(r"\s*NOT NULL\s*$", "", rest, flags=re.IGNORECASE).strip()
                cols.append((name, sql_type, not_null))
    return tables


# --- hbm parse (FK çıkarımı) ----------------------------------------------

CLASS_TABLE_RE = re.compile(
    r"<class\b[^>]*name=\"([^\"]+)\"[^>]*table=\"([^\"]+)\"",
    re.DOTALL,
)

M2O_RE = re.compile(
    r"<many-to-one\b(?P<attrs>[^>]*)>(?P<body>.*?)</many-to-one>",
    re.DOTALL,
)


def parse_hbm() -> tuple[dict[str, str], list[tuple[str, str, str]]]:
    """Returns (class→table, [(source_table, column, target_class)])."""
    class_to_table: dict[str, str] = {}
    m2o_raw: list[tuple[str, str, str]] = []  # (src_table, col, target_class)
    hbm_files = list((LEGACY / "TurquazStandAlone").rglob("*.hbm.xml"))
    for hf in hbm_files:
        text = hf.read_text(errors="ignore")
        cm = CLASS_TABLE_RE.search(text)
        if not cm:
            continue
        cls, table = cm.group(1), cm.group(2)
        class_to_table[cls] = table
        for m in M2O_RE.finditer(text):
            attrs = m.group("attrs")
            body = m.group("body")
            tcls_m = re.search(r"class=\"([^\"]+)\"", attrs)
            # önce <column name="..."/> ara, yoksa attribute olarak column="..."
            cn = re.search(r"<column\s+name=\"([^\"]+)\"", body)
            if not cn:
                cn = re.search(r"column=\"([^\"]+)\"", attrs)
            if tcls_m and cn:
                m2o_raw.append((table, cn.group(1), tcls_m.group(1)))
    return class_to_table, m2o_raw


# --- SQL üretimi -----------------------------------------------------------

HEADER = """\
-- Turquaz Resurrected — V1: temel şema
-- Üretildi: tools/gen_schema.py (deterministik). Elle düzenlemeyin; tekrar üretin.
-- Konvansiyonlar: ADR-0003.
--
-- Eski 2005/2018 turq_* tabloları modernize edildi:
--   * PK: id uuid (UUIDv7, uygulama tarafı üretim)
--   * FK'ler uuid
--   * Para: numeric(19,4); miktar/oran: numeric(19,6)
--   * Tarih/saat: timestamptz
--   * Çok kiracılılık: company_id uuid (sistem/katalog tabloları hariç)
--
SET client_min_messages = WARNING;
"""


def render_table(name: str, cols: list[tuple[str, str, bool]]) -> str:
    out: list[str] = []
    out.append(f"CREATE TABLE {name} (")
    body: list[str] = []
    seen_id = False
    for col, typ, not_null in cols:
        if col == "id":
            seen_id = True
        new_type = map_type(name, col, typ)
        body.append(f"    {col} {new_type}{' NOT NULL' if not_null else ''}")
    # company_id (kiracı kapsamı dışındakilere ekleme)
    if name not in GLOBAL_TABLES:
        body.append("    company_id uuid NOT NULL")
    out.append(",\n".join(body))
    out.append(");")
    # PK
    out.append(f"ALTER TABLE {name} ADD CONSTRAINT pk_{name[5:]} PRIMARY KEY (id);")
    # company_id indeksi
    if name not in GLOBAL_TABLES:
        out.append(f"CREATE INDEX ix_{name[5:]}_company ON {name} (company_id);")
    return "\n".join(out) + "\n"


def render_fks(fk_list: list[tuple[str, str, str]]) -> str:
    """fk_list: (src_table, src_col, tgt_table)"""
    out: list[str] = ["", "-- Yabancı anahtar kısıtları (hbm many-to-one'lardan türetildi)"]
    seen: set[tuple[str, str]] = set()
    for src, col, tgt in fk_list:
        key = (src, col)
        if key in seen:
            continue
        seen.add(key)
        # kısıt adı 63 karakter sınırını aşmasın
        cname = f"fk_{src[5:]}__{col}"[:63]
        out.append(
            f"ALTER TABLE {src} ADD CONSTRAINT {cname} "
            f"FOREIGN KEY ({col}) REFERENCES {tgt} (id);"
        )
        # FK indeksi
        iname = f"ix_{src[5:]}__{col}"[:63]
        out.append(f"CREATE INDEX {iname} ON {src} ({col});")
    return "\n".join(out) + "\n"


# 2018'de eklenen tek gerçek tablo (hbm'den modellendi).
CHEQUE_CHEQUES_ROLLS_DDL = """\
-- 2018'de eklenen ara tablo (çek ↔ portföy çoka-çok)
CREATE TABLE turq_cheque_cheques_rolls (
    id uuid NOT NULL,
    cheque_rolls_id uuid NOT NULL,
    cheque_cheques_id uuid NOT NULL,
    created_by varchar(50) NOT NULL,
    creation_date timestamptz NOT NULL,
    updated_by varchar(50) NOT NULL,
    last_modified timestamptz NOT NULL,
    company_id uuid NOT NULL
);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT pk_cheque_cheques_rolls PRIMARY KEY (id);
CREATE INDEX ix_cheque_cheques_rolls_company ON turq_cheque_cheques_rolls (company_id);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT fk_cheque_cheques_rolls__cheque_rolls_id FOREIGN KEY (cheque_rolls_id) REFERENCES turq_cheque_rolls (id);
ALTER TABLE turq_cheque_cheques_rolls ADD CONSTRAINT fk_cheque_cheques_rolls__cheque_cheques_id FOREIGN KEY (cheque_cheques_id) REFERENCES turq_cheque_cheques (id);
CREATE INDEX ix_cheque_cheques_rolls__cheque_rolls_id ON turq_cheque_cheques_rolls (cheque_rolls_id);
CREATE INDEX ix_cheque_cheques_rolls__cheque_cheques_id ON turq_cheque_cheques_rolls (cheque_cheques_id);
"""


def main() -> int:
    tables = parse_dump(DUMP)
    class_to_table, m2o_raw = parse_hbm()

    # FK listesini resolve et + sadece dump'ta bilinen tablolara referansları al
    fk_list: list[tuple[str, str, str]] = []
    for src, col, tgt_cls in m2o_raw:
        tgt = class_to_table.get(tgt_cls)
        if not tgt:
            continue
        if src not in tables:
            continue
        if tgt not in tables:
            # view/aggregate tablolarına referansları atla
            continue
        if col == "company_id":
            continue
        fk_list.append((src, col, tgt))
        HBM_FK_COLS.add((src, col))

    # Yazım sırası: turq_companies en başta (FK referansı için), sonra alfabetik
    ordered = ["turq_companies"] + sorted(t for t in tables if t != "turq_companies")

    parts: list[str] = [HEADER, ""]
    for t in ordered:
        parts.append(render_table(t, tables[t]))
    parts.append(CHEQUE_CHEQUES_ROLLS_DDL)
    # company_id global FK (turq_companies referansı)
    parts.append("-- company_id → turq_companies FK'leri")
    for t in ordered + ["turq_cheque_cheques_rolls"]:
        if t in GLOBAL_TABLES or t == "turq_companies":
            continue
        cname = f"fk_{t[5:]}__company_id"[:63]
        parts.append(
            f"ALTER TABLE {t} ADD CONSTRAINT {cname} "
            f"FOREIGN KEY (company_id) REFERENCES turq_companies (id);"
        )
    parts.append("")
    parts.append(render_fks(fk_list))

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text("\n".join(parts))
    print(f"Yazıldı: {OUT}")
    print(f"  Tablo sayısı: {len(ordered) + 1}")
    print(f"  FK sayısı   : {len({(s,c) for s,c,_ in fk_list})}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
