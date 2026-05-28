/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AdminApi;
import com.turquaz.desktop.rest.AdminApi.CompanyInfo;
import com.turquaz.desktop.rest.AdminApi.CreateCurrency;
import com.turquaz.desktop.rest.AdminApi.CreateExchangeRate;
import com.turquaz.desktop.rest.AdminApi.CurrencyDto;
import com.turquaz.desktop.rest.AdminApi.ExchangeRateDto;
import com.turquaz.desktop.rest.AdminApi.UpdateCompany;
import com.turquaz.desktop.rest.AdminApi.UserSummary;
import com.turquaz.desktop.rest.ApiException;
import java.time.LocalDate;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/** Ayarlar (admin) görünümleri: Şirket, Kullanıcılar, Para Birimleri, Döviz Kurları. */
public final class SettingsViews {

    private SettingsViews() {}

    // ===== Şirket =====

    public static class CompanyView {
        private final Composite parent;
        private final AdminApi api;
        private Text nameTx, addressTx, telTx, faxTx;

        public CompanyView(Composite parent, AdminApi api) {
            this.parent = parent;
            this.api = api;
            build();
            load();
        }

        private void build() {
            parent.setLayout(new GridLayout(1, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Şirket Bilgileri");

            Group g = new Group(parent, SWT.NONE);
            g.setText("Şirket");
            g.setLayout(new GridLayout(2, false));
            g.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            nameTx = SwtForms.textRow(g, "Ad");
            addressTx = SwtForms.textRow(g, "Adres");
            telTx = SwtForms.textRow(g, "Telefon");
            faxTx = SwtForms.textRow(g, "Faks");

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(actions, this::load);
            Button save = new Button(actions, SWT.PUSH);
            save.setText("Kaydet");
            save.addListener(SWT.Selection, e -> save());
        }

        private void load() {
            try {
                CompanyInfo c = api.getCompany();
                nameTx.setText(nz(c.name));
                addressTx.setText(nz(c.address));
                telTx.setText(nz(c.telephone));
                faxTx.setText(nz(c.fax));
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Yüklenemedi", ex.getMessage());
            }
        }

        private void save() {
            UpdateCompany req = new UpdateCompany();
            req.name = nameTx.getText().trim();
            req.address = addressTx.getText().trim();
            req.telephone = telTx.getText().trim();
            req.fax = faxTx.getText().trim();
            try {
                api.updateCompany(req);
                SwtForms.info(parent.getShell(), "Kaydedildi", "Şirket bilgileri güncellendi.");
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
            }
        }
    }

    // ===== Kullanıcılar =====

    public static class UsersView {
        private final Composite parent;
        private final AdminApi api;
        private Table table;

        public UsersView(Composite parent, AdminApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(1, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Kullanıcılar");

            table = SwtForms.makeTable(parent,
                    new String[] {"Kullanıcı Adı", "Gerçek Ad", "Açıklama"},
                    new int[] {200, 280, 280});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "kullanicilar", "Kullanıcılar");
            Button changePw = new Button(toolbar, SWT.PUSH);
            changePw.setText("Parolamı Değiştir...");
            changePw.addListener(SWT.Selection,
                    e -> new ChangePasswordDialog(parent.getShell(), api).open());
        }

        public void refresh() {
            try {
                List<UserSummary> list = api.listUsers();
                table.removeAll();
                for (UserSummary u : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(u.username), nz(u.realName), nz(u.description)
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    // ===== Para Birimleri =====

    public static class CurrenciesView {
        private final Composite parent;
        private final AdminApi api;
        private Table table;

        public CurrenciesView(Composite parent, AdminApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Para Birimleri");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Ad", "Kısaltma", "Ülke", "Varsayılan", "Sabit"},
                    new int[] {180, 100, 140, 100, 80});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Para Birimi");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text name = SwtForms.textRow(form, "Ad");
            Text abbr = SwtForms.textRow(form, "Kısaltma");
            Text country = SwtForms.textRow(form, "Ülke");
            Combo def = SwtForms.comboRow(form, "Varsayılan", new String[] {"Hayır", "Evet"});
            Combo constant = SwtForms.comboRow(form, "Sabit", new String[] {"Hayır", "Evet"});

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateCurrency r = new CreateCurrency();
                r.name = name.getText().trim();
                r.abbreviation = abbr.getText().trim();
                r.country = country.getText().trim();
                r.defaultCurrency = def.getSelectionIndex() == 1;
                r.constant = constant.getSelectionIndex() == 1;
                try {
                    api.createCurrency(r);
                    name.setText(""); abbr.setText(""); country.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "para_birimleri", "Para Birimleri");
        }

        public void refresh() {
            try {
                List<CurrencyDto> list = api.listCurrencies();
                table.removeAll();
                for (CurrencyDto c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(c.name), nz(c.abbreviation), nz(c.country),
                            Boolean.TRUE.equals(c.defaultCurrency) ? "✓" : "—",
                            Boolean.TRUE.equals(c.constant) ? "✓" : "—"
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    // ===== Döviz Kurları =====

    public static class ExchangeRatesView {
        private final Composite parent;
        private final AdminApi api;
        private Table table;

        public ExchangeRatesView(Composite parent, AdminApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Döviz Kurları");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Tarih", "Baz", "Hedef", "Kur"},
                    new int[] {120, 280, 280, 140});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Kur");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text base = SwtForms.textRow(form, "Baz Para UUID");
            Text target = SwtForms.textRow(form, "Hedef Para UUID");
            Text ratio = SwtForms.textRow(form, "Kur");
            Text date = SwtForms.textRow(form, "Tarih (YYYY-MM-DD)");
            date.setText(LocalDate.now().toString());

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateExchangeRate r = new CreateExchangeRate();
                r.baseCurrencyId = base.getText().trim();
                r.exchangeCurrencyId = target.getText().trim();
                r.exchangeRatio = SwtForms.parseDecimal(ratio.getText());
                try { r.date = LocalDate.parse(date.getText().trim()); }
                catch (Exception ex) {
                    SwtForms.error(parent.getShell(), "Tarih", "Geçersiz"); return;
                }
                try {
                    api.createExchangeRate(r);
                    ratio.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "doviz_kurlari", "Döviz Kurları");
        }

        public void refresh() {
            try {
                List<ExchangeRateDto> list = api.listExchangeRates();
                table.removeAll();
                for (ExchangeRateDto r : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            r.date == null ? "" : r.date.toString(),
                            nz(r.baseCurrencyId),
                            nz(r.exchangeCurrencyId),
                            r.exchangeRatio == null ? "" : r.exchangeRatio.toPlainString()
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    // ===== Parola Değiştirme Dialog'u =====

    public static class ChangePasswordDialog {
        private final Shell parent;
        private final AdminApi api;

        public ChangePasswordDialog(Shell parent, AdminApi api) {
            this.parent = parent;
            this.api = api;
        }

        public void open() {
            Shell dlg = new Shell(parent, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
            dlg.setText("Parolayı Değiştir");
            dlg.setLayout(new GridLayout(2, false));
            dlg.setSize(420, 220);

            new Label(dlg, SWT.NONE).setText("Mevcut Parola:");
            Text current = new Text(dlg, SWT.BORDER | SWT.PASSWORD);
            current.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            new Label(dlg, SWT.NONE).setText("Yeni Parola:");
            Text neu = new Text(dlg, SWT.BORDER | SWT.PASSWORD);
            neu.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            new Label(dlg, SWT.NONE).setText("Yeni (tekrar):");
            Text confirm = new Text(dlg, SWT.BORDER | SWT.PASSWORD);
            confirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            Label error = new Label(dlg, SWT.WRAP);
            error.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            error.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            Composite buttons = new Composite(dlg, SWT.NONE);
            buttons.setLayout(new org.eclipse.swt.layout.RowLayout());
            buttons.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            Button cancel = new Button(buttons, SWT.PUSH);
            cancel.setText("İptal");
            cancel.addListener(SWT.Selection, e -> dlg.close());
            Button ok = new Button(buttons, SWT.PUSH);
            ok.setText("Değiştir");
            dlg.setDefaultButton(ok);
            ok.addListener(SWT.Selection, e -> {
                if (!neu.getText().equals(confirm.getText())) {
                    error.setText("Yeni parola tekrarı eşleşmiyor");
                    return;
                }
                if (neu.getText().length() < 8) {
                    error.setText("Yeni parola en az 8 karakter olmalı");
                    return;
                }
                try {
                    api.changeMyPassword(current.getText(), neu.getText());
                    SwtForms.info(parent, "Parola", "Başarıyla değiştirildi.");
                    dlg.close();
                } catch (ApiException ex) {
                    error.setText(ex.getMessage());
                }
            });

            dlg.open();
            Display display = parent.getDisplay();
            while (!dlg.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
            }
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
