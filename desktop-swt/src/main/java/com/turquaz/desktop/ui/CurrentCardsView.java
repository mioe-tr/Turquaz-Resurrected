/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.ApiException;
import com.turquaz.desktop.rest.CurrentCardApi;
import com.turquaz.desktop.rest.CurrentCardApi.CreateRequest;
import com.turquaz.desktop.rest.CurrentCardApi.CurrentCard;
import java.math.BigDecimal;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Cari kart listesi + ekleme paneli. Eski TurquazClient'in CurCard listeleme
 * ve ekleme ekranlarının modern SWT karşılığı; veri REST üzerinden gelir.
 */
public class CurrentCardsView {

    private final Composite parent;
    private final CurrentCardApi api;
    private Table table;

    public CurrentCardsView(Composite parent, CurrentCardApi api) {
        this.parent = parent;
        this.api = api;
        build();
        refresh();
    }

    private void build() {
        parent.setLayout(new GridLayout(2, false));

        Label header = new Label(parent, SWT.NONE);
        header.setText("Cari Kartlar");
        header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        org.eclipse.swt.graphics.FontData[] fd = header.getFont().getFontData();
        for (var f : fd) { f.setHeight(14); f.setStyle(SWT.BOLD); }
        header.setFont(new org.eclipse.swt.graphics.Font(parent.getDisplay(), fd));

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.minimumWidth = 540;
        table.setLayoutData(tableData);

        String[] cols = {"Kod", "Ad", "Vergi No", "Kredi Limiti", "Risk Limiti"};
        int[] widths = {100, 220, 110, 110, 110};
        for (int i = 0; i < cols.length; i++) {
            TableColumn c = new TableColumn(table, SWT.NONE);
            c.setText(cols[i]);
            c.setWidth(widths[i]);
        }

        Composite form = buildForm(parent);
        form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        Composite toolbar = new Composite(parent, SWT.NONE);
        toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
        toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
        SwtForms.refreshButton(toolbar, this::refresh);
        SwtForms.exportButton(toolbar, table, "cari_kartlar", "Cari Kartlar");
    }

    private Composite buildForm(Composite parentComposite) {
        Group group = new Group(parentComposite, SWT.NONE);
        group.setText("Yeni Cari Kart");
        group.setLayout(new GridLayout(2, false));

        Text code = textRow(group, "Kod");
        Text name = textRow(group, "Ad");
        Text definition = textRow(group, "Açıklama");
        Text address = textRow(group, "Adres");
        Text taxDept = textRow(group, "Vergi Dairesi");
        Text taxNo = textRow(group, "Vergi No");
        Text credit = textRow(group, "Kredi Limiti");
        Text risk = textRow(group, "Risk Limiti");
        Text discRate = textRow(group, "İskonto Oranı");
        Text discPay = textRow(group, "İskonto Tutarı");
        Text days = textRow(group, "Vade Günü");

        credit.setText("0");
        risk.setText("0");
        discRate.setText("0");
        discPay.setText("0");
        days.setText("0");

        Button save = new Button(group, SWT.PUSH);
        save.setText("Kaydet");
        GridData saveData = new GridData(SWT.END, SWT.CENTER, true, false, 2, 1);
        save.setLayoutData(saveData);

        save.addListener(SWT.Selection, e -> {
            CreateRequest req = new CreateRequest();
            req.code = code.getText().trim();
            req.name = name.getText().trim();
            req.definition = definition.getText();
            req.address = address.getText();
            req.taxDepartment = taxDept.getText();
            req.taxNumber = taxNo.getText();
            req.creditLimit = parseDecimal(credit.getText());
            req.riskLimit = parseDecimal(risk.getText());
            req.discountRate = parseDecimal(discRate.getText());
            req.discountPayment = parseDecimal(discPay.getText());
            try {
                req.daysToValue = Integer.parseInt(days.getText().trim());
            } catch (NumberFormatException ex) {
                req.daysToValue = 0;
            }
            try {
                api.create(req);
                code.setText("");
                name.setText("");
                definition.setText("");
                address.setText("");
                taxDept.setText("");
                taxNo.setText("");
                refresh();
            } catch (ApiException ex) {
                show(SWT.ICON_ERROR, "Hata", ex.getMessage());
            }
        });

        return group;
    }

    private static Text textRow(Composite parent, String label) {
        new Label(parent, SWT.NONE).setText(label + ":");
        Text t = new Text(parent, SWT.BORDER);
        GridData d = new GridData(SWT.FILL, SWT.CENTER, true, false);
        d.widthHint = 180;
        t.setLayoutData(d);
        return t;
    }

    private static BigDecimal parseDecimal(String s) {
        try {
            return new BigDecimal(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public void refresh() {
        try {
            List<CurrentCard> cards = api.list();
            table.removeAll();
            for (CurrentCard c : cards) {
                TableItem item = new TableItem(table, SWT.NONE);
                item.setText(new String[] {
                        nz(c.code), nz(c.name), nz(c.taxNumber),
                        c.creditLimit == null ? "" : c.creditLimit.toPlainString(),
                        c.riskLimit == null ? "" : c.riskLimit.toPlainString()
                });
            }
        } catch (ApiException ex) {
            show(SWT.ICON_ERROR, "Liste alınamadı", ex.getMessage());
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private void show(int icon, String title, String message) {
        Shell shell = parent.getShell();
        MessageBox mb = new MessageBox(shell, icon | SWT.OK);
        mb.setText(title);
        mb.setMessage(message);
        mb.open();
    }
}
