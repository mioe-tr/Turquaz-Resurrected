/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AuthApi;
import com.turquaz.desktop.rest.CurrentCardApi;
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
 * Şimdilik Cari Kart + Pano görünümleri; diğerleri "yakında" olarak işaretli.
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
        shell.setSize(1100, 720);

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
        moduleList.setItems(
                "Pano",
                "Cari Kartlar",
                "Stok Kartları",
                "Depolar",
                "Yevmiye",
                "Mizan",
                "Banka",
                "Kasa",
                "Çek/Senet",
                "Faturalar",
                "Siparişler",
                "Konsinye",
                "Stok Kâr Analizi");

        Composite content = new Composite(sash, SWT.NONE);
        StackLayout stack = new StackLayout();
        content.setLayout(stack);

        Composite dashboard = placeholder(content, "Pano", "Soldaki listeden bir modül seçin.");
        Composite cariView = new Composite(content, SWT.NONE);
        cariView.setLayout(new FillLayout());
        new CurrentCardsView(cariView, new CurrentCardApi(rest));
        Composite comingSoon = placeholder(content, "Yakında", "Bu modül henüz masaüstüne taşınmadı. Web istemcisini deneyin.");

        stack.topControl = dashboard;
        moduleList.select(0);
        moduleList.addListener(SWT.Selection, e -> {
            int sel = moduleList.getSelectionIndex();
            switch (sel) {
                case 0 -> stack.topControl = dashboard;
                case 1 -> stack.topControl = cariView;
                default -> stack.topControl = comingSoon;
            }
            content.layout();
        });

        sash.setWeights(new int[] {1, 4});

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    private Composite placeholder(Composite parent, String title, String message) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(1, false));
        Label l1 = new Label(c, SWT.NONE);
        l1.setText(title);
        Label l2 = new Label(c, SWT.WRAP);
        l2.setText(message);
        l2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        return c;
    }
}
