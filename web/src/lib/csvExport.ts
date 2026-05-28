/*
 * Turquaz Resurrected — GPLv3
 * Bir dizi nesneyi UTF-8 BOM'lu CSV olarak indirir.
 * (Excel UTF-8 CSV'yi BOM ile doğru tanır.)
 */

export interface CsvColumn<T> {
  header: string;
  value: (row: T) => string | number | boolean | null | undefined;
}

export function downloadCsv<T>(filename: string, columns: CsvColumn<T>[], rows: T[]) {
  const escape = (v: unknown): string => {
    if (v === null || v === undefined) return "";
    const s = String(v);
    if (s.includes(";") || s.includes('"') || s.includes("\n") || s.includes("\r")) {
      return `"${s.replace(/"/g, '""')}"`;
    }
    return s;
  };

  const lines: string[] = [];
  lines.push(columns.map((c) => escape(c.header)).join(";"));
  for (const row of rows) {
    lines.push(columns.map((c) => escape(c.value(row))).join(";"));
  }
  const csv = "﻿" + lines.join("\r\n"); // UTF-8 BOM

  const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename.endsWith(".csv") ? filename : `${filename}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}
