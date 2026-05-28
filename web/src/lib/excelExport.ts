/*
 * Turquaz Resurrected — GPLv3
 * SheetJS ile gerçek .xlsx Excel dışa aktarma.
 */
import * as XLSX from "xlsx";

export interface SheetColumn<T> {
  header: string;
  value: (row: T) => string | number | boolean | null | undefined;
}

export function downloadExcel<T>(
  filename: string,
  sheetName: string,
  columns: SheetColumn<T>[],
  rows: T[],
) {
  const aoa: (string | number | boolean | null)[][] = [];
  aoa.push(columns.map((c) => c.header));
  for (const row of rows) {
    aoa.push(
      columns.map((c) => {
        const v = c.value(row);
        if (v === undefined || v === null) return "";
        return v as string | number | boolean;
      }),
    );
  }
  const ws = XLSX.utils.aoa_to_sheet(aoa);

  // Sütun genişlikleri (yaklaşık karakter sayısına göre)
  ws["!cols"] = columns.map((col) => {
    let max = col.header.length;
    for (const row of rows) {
      const s = String(col.value(row) ?? "");
      if (s.length > max) max = s.length;
    }
    return { wch: Math.min(60, Math.max(8, max + 2)) };
  });

  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, sheetName.slice(0, 31));
  const out = filename.endsWith(".xlsx") ? filename : `${filename}.xlsx`;
  XLSX.writeFile(wb, out);
}
