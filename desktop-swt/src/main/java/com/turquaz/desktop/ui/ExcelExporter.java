/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * SWT {@link Table} içeriğini Excel'e (.xlsx) ya da CSV'ye aktaran yardımcı.
 *
 * <p>Kullanım:
 * <pre>{@code
 * Button btn = new Button(parent, SWT.PUSH);
 * btn.setText("Excel'e Aktar");
 * btn.addListener(SWT.Selection, e ->
 *     ExcelExporter.exportTable(parent.getShell(), table, "cariler", "Cari Kartlar"));
 * }</pre>
 */
public final class ExcelExporter {

    private ExcelExporter() {}

    /**
     * Tablonun mevcut görünür içeriğini bir .xlsx dosyasına aktarır.
     * Kullanıcıya FileDialog ile kaydetme yeri sorulur.
     */
    public static void exportTable(Shell parent, Table table, String defaultBaseName, String sheetName) {
        FileDialog dlg = new FileDialog(parent, SWT.SAVE);
        dlg.setText("Excel'e Aktar");
        dlg.setFilterExtensions(new String[] {"*.xlsx", "*.csv"});
        dlg.setFilterNames(new String[] {"Excel Çalışma Kitabı (*.xlsx)", "CSV (*.csv)"});
        dlg.setFileName(defaultBaseName + ".xlsx");
        dlg.setOverwrite(true);
        String path = dlg.open();
        if (path == null) return;

        try {
            if (path.toLowerCase().endsWith(".csv")) {
                writeCsv(table, path);
            } else {
                writeXlsx(table, path, sheetName);
            }
            SwtForms.info(parent, "Dışa Aktarma", "Kaydedildi:\n" + path);
        } catch (IOException ex) {
            SwtForms.error(parent, "Dışa Aktarma Hatası", ex.getMessage());
        }
    }

    private static void writeXlsx(Table table, String path, String sheetName) throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(path)) {

            Sheet sheet = wb.createSheet(sheetName == null || sheetName.isBlank() ? "Sayfa1" : sheetName);

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            TableColumn[] cols = table.getColumns();
            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i].getText());
                c.setCellStyle(headerStyle);
            }

            TableItem[] items = table.getItems();
            for (int r = 0; r < items.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < cols.length; c++) {
                    String val = items[r].getText(c);
                    Cell cell = row.createCell(c);
                    // Sayısal değer ise number olarak yaz, değilse string
                    Double num = tryParseDouble(val);
                    if (num != null) {
                        cell.setCellValue(num);
                    } else {
                        cell.setCellValue(val);
                    }
                }
            }

            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
        }
    }

    private static void writeCsv(Table table, String path) throws IOException {
        TableColumn[] cols = table.getColumns();
        TableItem[] items = table.getItems();
        try (java.io.PrintWriter w = new java.io.PrintWriter(
                new java.io.OutputStreamWriter(new FileOutputStream(path),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            // BOM — Excel UTF-8 CSV'yi doğru tanısın
            w.print('﻿');
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) w.print(';');
                w.print(escape(cols[i].getText()));
            }
            w.println();
            for (TableItem it : items) {
                for (int c = 0; c < cols.length; c++) {
                    if (c > 0) w.print(';');
                    w.print(escape(it.getText(c)));
                }
                w.println();
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(";") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static Double tryParseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        String clean = s.replace(".", "").replace(",", ".");
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
