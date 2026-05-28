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
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Cari kart listesi + ekleme. "+ Yeni Cari Kart" butonu modal dialog açar.
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
        parent.setLayout(new GridLayout(1, false));

        // Üst başlık + Yeni butonu
        Composite header = new Composite(parent, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label title = new Label(header, SWT.NONE);
        title.setText("Cari Kartlar");
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        org.eclipse.swt.graphics.FontData[] fd = title.getFont().getFontData();
        for (var f : fd) { f.setHeight(14); f.setStyle(SWT.BOLD); }
        title.setFont(new org.eclipse.swt.graphics.Font(parent.getDisplay(), fd));

        Composite headerActions = new Composite(header, SWT.NONE);
        headerActions.setLayout(new org.eclipse.swt.layout.RowLayout(SWT.HORIZONTAL));
        SwtForms.primaryAddButton(headerActions, "Yeni Cari Kart", this::openNewDialog);

        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        String[] cols = {"Kod", "Ad", "Vergi No", "Kredi Limiti", "Risk Limiti"};
        int[] widths = {120, 280, 120, 130, 130};
        for (int i = 0; i < cols.length; i++) {
            TableColumn c = new TableColumn(table, SWT.NONE);
            c.setText(cols[i]);
            c.setWidth(widths[i]);
        }

        // Alt araç çubuğu
        Composite toolbar = new Composite(parent, SWT.NONE);
        toolbar.setLayout(new org.eclipse.swt.layout.RowLayout(SWT.HORIZONTAL));
        toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
        SwtForms.refreshButton(toolbar, this::refresh);
        SwtForms.exportButton(toolbar, table, "cari_kartlar", "Cari Kartlar");
    }

    private void openNewDialog() {
        Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Cari Kart", 520, 600);

        Text code = SwtForms.textRow(dlg, "Kod");
        Text name = SwtForms.textRow(dlg, "Ad");
        Text definition = SwtForms.textRow(dlg, "Açıklama");
        Text address = SwtForms.textRow(dlg, "Adres");
        Text taxDept = SwtForms.textRow(dlg, "Vergi Dairesi");
        Text taxNo = SwtForms.textRow(dlg, "Vergi No");
        Text credit = SwtForms.textRow(dlg, "Kredi Limiti");
        Text risk = SwtForms.textRow(dlg, "Risk Limiti");
        Text discRate = SwtForms.textRow(dlg, "İskonto Oranı");
        Text discPay = SwtForms.textRow(dlg, "İskonto Tutarı");
        Text days = SwtForms.textRow(dlg, "Vade Günü");

        credit.setText("0"); risk.setText("0"); discRate.setText("0");
        discPay.setText("0"); days.setText("0");

        SwtForms.dialogButtonBar(dlg, () -> {
            CreateRequest req = new CreateRequest();
            req.code = code.getText().trim();
            req.name = name.getText().trim();
            req.definition = definition.getText();
            req.address = address.getText();
            req.taxDepartment = taxDept.getText();
            req.taxNumber = taxNo.getText();
            req.creditLimit = SwtForms.parseDecimal(credit.getText());
            req.riskLimit = SwtForms.parseDecimal(risk.getText());
            req.discountRate = SwtForms.parseDecimal(discRate.getText());
            req.discountPayment = SwtForms.parseDecimal(discPay.getText());
            req.daysToValue = SwtForms.parseInt(days.getText(), 0);
            try {
                api.create(req);
                refresh();
                return true;
            } catch (ApiException ex) {
                SwtForms.error(dlg, "Hata", ex.getMessage());
                return false;
            }
        });

        SwtForms.runModal(dlg);
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
            SwtForms.error(parent.getShell(), "Liste alınamadı", ex.getMessage());
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
