#!/usr/bin/env python3
"""
V1__sema.sql'den JPA entity'leri ve Spring Data repository'lerini üretir.

Konvansiyonlar: ADR-0003.
- BaseEntity              : audit yok, global
- BaseCompanyScopedEntity : audit yok, kiracıya özel
- AuditableEntity         : audit (last_modified) var, global
- CompanyScopedEntity     : audit (last_modified) var, kiracıya özel
- update_date kullananlarda @AttributeOverride(lastModified → update_date)

Sınıf adı: turq_<...> → <PascalCase singular>.
Paket: modüle göre (accounting, current, inventory, bank, ...).
"""
from __future__ import annotations
import re
import sys
from pathlib import Path

ROOT = Path("/home/user/Turquaz-Resurrected")
SCHEMA = ROOT / "persistence/src/main/resources/db/migration/V1__sema.sql"
ENTITY_ROOT = ROOT / "persistence/src/main/java/com/turquaz/persistence"

LICENSE = """\
/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
"""

GLOBAL_TABLES = {
    "turq_companies",
    "turq_modules",
    "turq_module_components",
    "turq_engine_menu",
}

# Tablo → modül paketi (turq_ ön eki düşürülmüş prefix'e göre)
MODULE_RULES = [
    ("accounting_", "accounting"),
    ("bank_", "bank"),
    ("banks_", "bank"),
    ("bill_", "bill"),
    ("bills", "bill"),
    ("tradebill_", "bill"),
    ("order", "bill"),
    ("services", "bill"),
    ("cash_", "cash"),
    ("cheque_", "cheque"),
    ("consignment", "consignment"),
    ("consignments", "consignment"),
    ("current_", "current"),
    ("inventory_", "inventory"),
    ("companies", "common"),
    ("currencies", "common"),
    ("currency_", "common"),
    ("modules", "engine"),
    ("module_", "engine"),
    ("engine_", "engine"),
    ("settings", "engine"),
    ("users", "admin"),
    ("user_", "admin"),
    ("groups", "admin"),
    ("group_", "admin"),
]


def module_for(table: str) -> str:
    stem = table[len("turq_") :]
    for prefix, mod in MODULE_RULES:
        if stem.startswith(prefix) or stem == prefix.rstrip("_"):
            return mod
    raise RuntimeError(f"Bilinmeyen modül: {table}")


def singularize(word: str) -> str:
    if word.endswith("ies"):
        return word[:-3] + "y"
    if word.endswith(("sses", "xes", "ches", "shes", "zes")):
        return word[:-2]
    if word.endswith("s") and not word.endswith("ss"):
        return word[:-1]
    return word


def pascal_class(table: str) -> str:
    stem = table[len("turq_") :]
    parts = stem.split("_")
    parts[-1] = singularize(parts[-1])
    return "".join(p[:1].upper() + p[1:] for p in parts)


def camel(name: str) -> str:
    parts = name.split("_")
    return parts[0] + "".join(p[:1].upper() + p[1:] for p in parts[1:])


# Sütun tipi → Java tipi
def java_type(sql: str) -> tuple[str, str]:
    """returns (java_type, import_fqn or '')"""
    s = sql.strip().lower()
    if s == "uuid":
        return "UUID", "java.util.UUID"
    if s.startswith("integer"):
        return "Integer", ""
    if s.startswith("smallint"):
        return "Short", ""
    if s.startswith("bigint"):
        return "Long", ""
    if s.startswith("numeric"):
        return "BigDecimal", "java.math.BigDecimal"
    if s.startswith("varchar") or s == "text":
        return "String", ""
    if s.startswith("boolean"):
        return "Boolean", ""
    if s.startswith("timestamptz") or s.startswith("timestamp"):
        return "Instant", "java.time.Instant"
    raise RuntimeError(f"Bilinmeyen SQL tipi: {sql}")


# V1 şemasını ayrıştır
TABLE_RE = re.compile(r"^CREATE TABLE (\S+) \(\s*$")
COL_RE = re.compile(r"^\s+([a-z_][a-z0-9_]*)\s+(.+?)(\s+NOT NULL)?,?\s*$", re.IGNORECASE)


def parse_schema(path: Path):
    tables: dict[str, list[tuple[str, str, bool]]] = {}
    cur = None
    cols: list[tuple[str, str, bool]] = []
    with path.open() as f:
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
                if not mc:
                    continue
                name = mc.group(1)
                not_null = bool(re.search(r"\bNOT NULL\b", line, re.IGNORECASE))
                sql_type = mc.group(2).rstrip(",").strip()
                sql_type = re.sub(r"\s*NOT NULL\s*$", "", sql_type, flags=re.IGNORECASE).strip()
                cols.append((name, sql_type, not_null))
    return tables


AUDIT_COLS = {"created_by", "creation_date", "updated_by"}
SCOPE_COL = "company_id"
ID_COL = "id"


def audit_kind(cols: list[tuple[str, str, bool]]) -> str:
    names = {c[0] for c in cols}
    if not (AUDIT_COLS <= names):
        return "none"
    if "last_modified" in names:
        return "last_modified"
    if "update_date" in names:
        return "update_date"
    return "none"


def base_class(table: str, kind: str) -> str:
    scoped = table not in GLOBAL_TABLES
    if kind == "none":
        return "BaseCompanyScopedEntity" if scoped else "BaseEntity"
    return "CompanyScopedEntity" if scoped else "AuditableEntity"


def emit_entity(table: str, cols, kind: str, module: str) -> str:
    cls = pascal_class(table)
    base = base_class(table, kind)
    excluded = {ID_COL} | AUDIT_COLS | {"last_modified", "update_date"}
    if table not in GLOBAL_TABLES:
        excluded.add(SCOPE_COL)

    imports: set[str] = {
        "jakarta.persistence.Column",
        "jakarta.persistence.Entity",
        "jakarta.persistence.Table",
    }
    annotations: list[str] = []
    if base.startswith("Auditable") or base.startswith("CompanyScoped") or base.startswith("BaseCompany") or base == "BaseEntity":
        imports.add(f"com.turquaz.persistence.{base}")

    if kind == "update_date":
        imports.add("jakarta.persistence.AttributeOverride")
        annotations.append(
            "@AttributeOverride(name = \"lastModified\", column = @Column(name = \"update_date\", nullable = false))"
        )

    fields: list[str] = []
    accessors: list[str] = []
    for col, sql_type, not_null in cols:
        if col in excluded:
            continue
        jt, imp = java_type(sql_type)
        if imp:
            imports.add(imp)
        field = camel(col)
        # length for varchar(n)
        length_attr = ""
        m_len = re.match(r"^varchar\((\d+)\)$", sql_type, re.IGNORECASE)
        if m_len:
            length_attr = f", length = {m_len.group(1)}"
        nullable_attr = "" if not_null else ""  # @Column default nullable=true; we set nullable=false when NOT NULL
        if not_null:
            nullable_attr = ", nullable = false"
        fields.append(
            f"    @Column(name = \"{col}\"{nullable_attr}{length_attr})\n"
            f"    private {jt} {field};\n"
        )
        cap = field[:1].upper() + field[1:]
        accessors.append(
            f"    public {jt} get{cap}() {{ return {field}; }}\n"
            f"    public void set{cap}({jt} v) {{ this.{field} = v; }}\n"
        )

    import_block = "\n".join(f"import {i};" for i in sorted(imports))
    ann_block = ("\n" + "\n".join(annotations)) if annotations else ""

    body = "\n".join(fields) + ("\n" + "\n".join(accessors) if accessors else "")
    return f"""{LICENSE}package com.turquaz.persistence.{module};

{import_block}

@Entity
@Table(name = "{table}"){ann_block}
public class {cls} extends {base} {{

{body}
}}
"""


def emit_repository(table: str, module: str) -> str:
    cls = pascal_class(table)
    return f"""{LICENSE}package com.turquaz.persistence.{module};

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface {cls}Repository extends JpaRepository<{cls}, UUID> {{
}}
"""


def main() -> int:
    tables = parse_schema(SCHEMA)
    print(f"Tablo sayısı: {len(tables)}")
    n_entity = n_repo = 0
    for table, cols in sorted(tables.items()):
        module = module_for(table)
        pkg_dir = ENTITY_ROOT / module
        pkg_dir.mkdir(parents=True, exist_ok=True)
        cls = pascal_class(table)
        kind = audit_kind(cols)
        (pkg_dir / f"{cls}.java").write_text(emit_entity(table, cols, kind, module))
        (pkg_dir / f"{cls}Repository.java").write_text(emit_repository(table, module))
        n_entity += 1
        n_repo += 1
    print(f"Entity: {n_entity}, Repository: {n_repo}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
