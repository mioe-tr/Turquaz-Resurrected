/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AccountingApi;
import com.turquaz.desktop.rest.AdminApi;
import com.turquaz.desktop.rest.ApiException;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

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

        buildMenuBar(shell);
        ToolBar toolbar = buildToolBar(shell);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

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
                "Stok Kâr Analizi",
                "— Ayarlar —",
                "Şirket Bilgisi",
                "Kullanıcılar",
                "Para Birimleri",
                "Döviz Kurları"
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

        buildStatusBar(shell);

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
            // 15 — "— Ayarlar —" başlığı (tıklanamaz olmalı; aşağıda placeholder)
            case 15 -> {
                slot.setLayout(new GridLayout(1, false));
                Label l = new Label(slot, SWT.NONE);
                l.setText("Ayarlar bölümü — alttaki maddelerden seçin.");
            }
            case 16 -> new SettingsViews.CompanyView(slot, new AdminApi(rest));
            case 17 -> new SettingsViews.UsersView(slot, new AdminApi(rest));
            case 18 -> new SettingsViews.CurrenciesView(slot, new AdminApi(rest));
            case 19 -> new SettingsViews.ExchangeRatesView(slot, new AdminApi(rest));
            default -> {
                slot.setLayout(new GridLayout(1, false));
                Label l = new Label(slot, SWT.NONE);
                l.setText("Bilinmeyen modül.");
            }
        }
    }

    private void buildMenuBar(Shell shell) {
        Menu bar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(bar);

        // Dosya
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("&Dosya");
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        fileItem.setMenu(fileMenu);
        addItem(fileMenu, "Çıkış\tAlt+F4", SWT.MOD3 | SWT.F4, e -> shell.close());

        // Düzen
        MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
        editItem.setText("D&üzen");
        Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
        editItem.setMenu(editMenu);
        addItem(editMenu, "Yenile\tF5", SWT.F5, e -> {
            // Aktif görünüm bilgisi yok; kullanıcı genelde refresh butonunu basabilir.
            // Burada placeholder olarak shell başlığını güncelle.
            shell.setText("Turquaz Resurrected — " + session.username);
        });

        // Görünüm
        MenuItem viewItem = new MenuItem(bar, SWT.CASCADE);
        viewItem.setText("&Görünüm");
        Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
        viewItem.setMenu(viewMenu);
        // Görünüm menüsü, modül listesindeki seçimi koruyup yön verir.
        addItem(viewMenu, "Pano", 0, null);
        addItem(viewMenu, "Cari Kartlar", 0, null);
        addItem(viewMenu, "Stok Kartları", 0, null);
        new MenuItem(viewMenu, SWT.SEPARATOR);
        addItem(viewMenu, "Yevmiye", 0, null);
        addItem(viewMenu, "Mizan", 0, null);

        // Araçlar
        MenuItem toolsItem = new MenuItem(bar, SWT.CASCADE);
        toolsItem.setText("&Araçlar");
        Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
        toolsItem.setMenu(toolsMenu);
        addItem(toolsMenu, "Şirket Bilgisi", 0, null);
        addItem(toolsMenu, "Kullanıcılar", 0, null);
        addItem(toolsMenu, "Para Birimleri", 0, null);
        addItem(toolsMenu, "Döviz Kurları", 0, null);
        new MenuItem(toolsMenu, SWT.SEPARATOR);
        addItem(toolsMenu, "Parolayı Değiştir...", 0, e ->
                new SettingsViews.ChangePasswordDialog(shell, new AdminApi(rest)).open());

        // Yardım
        MenuItem helpItem = new MenuItem(bar, SWT.CASCADE);
        helpItem.setText("&Yardım");
        Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
        helpItem.setMenu(helpMenu);
        addItem(helpMenu, "Hakkında", 0, e -> {
            MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            mb.setText("Turquaz Resurrected Hakkında");
            mb.setMessage("""
                    Turquaz Resurrected — Modern Türk muhasebe yazılımı
                    Sürüm: 0.9.0-SNAPSHOT
                    Lisans: GPLv3
                    Özgün ekibe (2003-2018) saygılarımızla.""");
            mb.open();
        });
    }

    private static void addItem(Menu menu, String text, int accelerator,
                                org.eclipse.swt.widgets.Listener listener) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(text);
        if (accelerator != 0) mi.setAccelerator(accelerator);
        if (listener != null) mi.addListener(SWT.Selection, listener);
    }

    /** Üst araç çubuğu — sık kullanılan eylemler için Unicode ikonlu butonlar. */
    private ToolBar buildToolBar(Shell shell) {
        ToolBar tb = new ToolBar(shell, SWT.FLAT | SWT.WRAP | SWT.RIGHT);

        addTool(tb, "🔄  Yenile", "Görünümü yenile (F5)", null);
        addTool(tb, "📊  Mizan", "Mizanı aç", e -> { /* MainShell modül seçimi */ });
        addTool(tb, "📒  Yevmiye", "Yevmiye fişi", null);
        new ToolItem(tb, SWT.SEPARATOR);
        addTool(tb, "👥  Cari", "Cari kartlar", null);
        addTool(tb, "📦  Stok", "Stok kartları", null);
        addTool(tb, "🧾  Fatura", "Faturalar", null);
        new ToolItem(tb, SWT.SEPARATOR);
        addTool(tb, "⚙  Ayarlar", "Şirket / Kullanıcılar / Para", null);
        addTool(tb, "🔑  Parola", "Parolayı değiştir",
                e -> new SettingsViews.ChangePasswordDialog(shell, new AdminApi(rest)).open());
        new ToolItem(tb, SWT.SEPARATOR);
        addTool(tb, "❌  Çıkış", "Uygulamayı kapat", e -> shell.close());

        return tb;
    }

    private static void addTool(ToolBar tb, String text, String tooltip,
                                org.eclipse.swt.widgets.Listener listener) {
        ToolItem it = new ToolItem(tb, SWT.PUSH);
        it.setText(text);
        if (tooltip != null) it.setToolTipText(tooltip);
        if (listener != null) it.addListener(SWT.Selection, listener);
    }

    /** Alt durum çubuğu — kullanıcı, şirket, API URL ve bağlantı durumu. */
    private void buildStatusBar(Shell shell) {
        Composite status = new Composite(shell, SWT.BORDER);
        status.setLayout(new GridLayout(4, false));
        status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label connLabel = new Label(status, SWT.NONE);
        connLabel.setText("● Bağlı");
        connLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));

        Label sep1 = new Label(status, SWT.SEPARATOR | SWT.VERTICAL);
        sep1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

        Label userLabel = new Label(status, SWT.NONE);
        userLabel.setText("Kullanıcı: " + session.username + "  |  Şirket: " + session.companyId);

        Label apiLabel = new Label(status, SWT.NONE);
        apiLabel.setText("API: " + System.getenv().getOrDefault("TURQUAZ_API_URL", "http://localhost:8080"));
        apiLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));

        // 30 saniyede bir basit bağlantı kontrolü
        Runnable check = new Runnable() {
            @Override
            public void run() {
                if (shell.isDisposed()) return;
                try {
                    new AdminApi(rest).getCompany();
                    connLabel.setText("● Bağlı");
                    connLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
                } catch (ApiException ex) {
                    connLabel.setText("● Bağlantı yok");
                    connLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
                }
                shell.getDisplay().timerExec(30_000, this);
            }
        };
        shell.getDisplay().timerExec(30_000, check);
    }
}
