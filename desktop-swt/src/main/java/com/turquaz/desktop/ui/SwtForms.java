/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import java.math.BigDecimal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/** Görünümler arasında paylaşılan ufak SWT yardımcıları. */
public final class SwtForms {

    private SwtForms() {}

    public static Text textRow(Composite parent, String label) {
        new Label(parent, SWT.NONE).setText(label + ":");
        Text t = new Text(parent, SWT.BORDER);
        GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false);
        d.widthHint = 180;
        t.setLayoutData(d);
        return t;
    }

    public static Combo comboRow(Composite parent, String label, String[] items) {
        new Label(parent, SWT.NONE).setText(label + ":");
        Combo c = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
        c.setItems(items);
        if (items.length > 0) c.select(0);
        GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false);
        d.widthHint = 180;
        c.setLayoutData(d);
        return c;
    }

    public static Table makeTable(Composite parent, String[] columns, int[] widths) {
        Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        for (int i = 0; i < columns.length; i++) {
            TableColumn c = new TableColumn(table, SWT.NONE);
            c.setText(columns[i]);
            c.setWidth(widths[i]);
        }
        return table;
    }

    public static BigDecimal parseDecimal(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    public static void error(Shell shell, String title, String message) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        mb.setText(title);
        mb.setMessage(message);
        mb.open();
    }

    public static void info(Shell shell, String title, String message) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
        mb.setText(title);
        mb.setMessage(message);
        mb.open();
    }

    /** Tabloya bağlı "Excel'e Aktar" butonu üretir. */
    public static org.eclipse.swt.widgets.Button exportButton(
            Composite parent, Table table, String baseName, String sheetName) {
        org.eclipse.swt.widgets.Button btn = new org.eclipse.swt.widgets.Button(parent, SWT.PUSH);
        btn.setText("Excel'e Aktar");
        btn.addListener(SWT.Selection,
                e -> ExcelExporter.exportTable(parent.getShell(), table, baseName, sheetName));
        return btn;
    }

    /** "Yenile" butonu üretir; verilen aksiyon her tıklamada çalışır. */
    public static org.eclipse.swt.widgets.Button refreshButton(
            Composite parent, Runnable action) {
        org.eclipse.swt.widgets.Button btn = new org.eclipse.swt.widgets.Button(parent, SWT.PUSH);
        btn.setText("Yenile");
        btn.addListener(SWT.Selection, e -> action.run());
        return btn;
    }
}
