/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AccountingApi;
import com.turquaz.desktop.rest.AuthApi;
import com.turquaz.desktop.rest.BankCashChequeApi;
import com.turquaz.desktop.rest.BillApi;
import com.turquaz.desktop.rest.CurrentCardApi;
import com.turquaz.desktop.rest.InventoryApi;
import com.turquaz.desktop.rest.RestClient;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * Eski TurquazClient WorkbenchWindow'unun modern SWT karşılığı:
 * sol tarafta modül listesi, sağda seçilen modülün görünümü (StackLayout).
 * Her görünüm ilk açılışta lazy olarak yaratılır.
 */
public class MainShell {

    private final Display display;
    private final RestClient rest;
    private final AuthApi.LoginResponse session;

    public MainShell(Display display, RestClient rest, AuthApi.LoginResponse session) {
        this.display = display;
        this.rest = rest;
        this.session = session;
    }

    public void open() {
        Shell shell = new Shell(display);
        shell.setText("Turquaz Resurrected — " + session.username);
        shell.setLayout(new GridLayout(1, false));
        shell.setSize(1280, 800);

        Composite header = new Composite(shell, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Label title = new Label(header, SWT.NONE);
        title.setText("Turquaz Resurrected");
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        Label user = new Label(header, SWT.NONE);
        user.setText("Giriş yapan: " + session.username);

        SashForm sash = new SashForm(shell, SWT.HORIZONTAL);
        sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        List moduleList = new List(sash, SWT.BORDER | SWT.V_SCROLL);
        String[] labels = {
                "Pano",
                "Cari Kartlar",
                "Stok Kartları",
                "Depolar",
                "Stok Bakiyesi",
                "Yevmiye",
                "Mizan",
                "Hesap Bakiyesi",
                "Banka",
                "Kasa",
                "Çek/Senet",
                "Faturalar",
                "Siparişler",
                "Konsinye",
                "Stok Kâr Analizi"
        };
        moduleList.setItems(labels);

        Composite content = new Composite(sash, SWT.NONE);
        StackLayout stack = new StackLayout();
        content.setLayout(stack);

        // Lazy yaratım için her slot bir Composite, ilk seçildiğinde içerik konulur.
        Composite[] slots = new Composite[labels.length];
        boolean[] created = new boolean[labels.length];
        for (int i = 0; i < labels.length; i++) {
            slots[i] = new Composite(content, SWT.NONE);
            slots[i].setLayout(new FillLayout());
        }

        // Pano (slot 0) hep doludur — basit özet.
        new DashboardView(slots[0], session);
        created[0] = true;

        stack.topControl = slots[0];
        moduleList.select(0);

        moduleList.addListener(SWT.Selection, e -> {
            int sel = moduleList.getSelectionIndex();
            if (!created[sel]) {
                createView(sel, slots[sel]);
                created[sel] = true;
            }
            stack.topControl = slots[sel];
            content.layout();
        });

        sash.setWeights(new int[] {1, 5});

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    private void createView(int index, Composite slot) {
        switch (index) {
            case 1 -> new CurrentCardsView(slot, new CurrentCardApi(rest));
            case 2 -> new InventoryViews.CardsView(slot, new InventoryApi(rest));
            case 3 -> new InventoryViews.WarehousesView(slot, new InventoryApi(rest));
            case 4 -> new InventoryViews.StockOnHandView(slot, new InventoryApi(rest));
            case 5 -> new AccountingViews.JournalEntryView(slot, new AccountingApi(rest));
            case 6 -> new AccountingViews.TrialBalanceView(slot, new AccountingApi(rest));
            case 7 -> new AccountingViews.AccountBalanceView(slot, new AccountingApi(rest));
            case 8 -> new BankCashChequeViews.BankCardsView(slot, new BankCashChequeApi(rest));
            case 9 -> new BankCashChequeViews.CashCardsView(slot, new BankCashChequeApi(rest));
            case 10 -> new BankCashChequeViews.ChequesView(slot, new BankCashChequeApi(rest));
            case 11 -> new BillViews.BillsView(slot, new BillApi(rest));
            case 12 -> new BillViews.OrdersView(slot, new BillApi(rest));
            case 13 -> new BillViews.ConsignmentsView(slot, new BillApi(rest));
            case 14 -> new BillViews.InventoryProfitView(slot, new BillApi(rest));
            default -> {
                slot.setLayout(new GridLayout(1, false));
                Label l = new Label(slot, SWT.NONE);
                l.setText("Bilinmeyen modül.");
            }
        }
    }
}
