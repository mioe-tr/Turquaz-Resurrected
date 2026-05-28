/*
 * Turquaz Resurrected — GPLv3
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public final class SettingsViews {

    private SettingsViews() {}

    // ===== Şirket — form (tek satırlık, yeni eklenmiyor; yalnız güncelleme) =====

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
            parent.setLayout(new GridLayout(2, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Şirket Bilgileri");
            h.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            nameTx = SwtForms.textRow(parent, "Ad");
            addressTx = SwtForms.textRow(parent, "Adres");
            telTx = SwtForms.textRow(parent, "Telefon");
            faxTx = SwtForms.textRow(parent, "Faks");

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
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
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Kullanıcılar");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            Button changePw = new Button(actions, SWT.PUSH);
            changePw.setText("🔑  Parolamı Değiştir...");
            changePw.addListener(SWT.Selection,
                    e -> new ChangePasswordDialog(parent.getShell(), api).open());

            table = SwtForms.makeTable(parent,
                    new String[] {"Kullanıcı Adı", "Gerçek Ad", "Açıklama"},
                    new int[] {220, 320, 320});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "kullanicilar", "Kullanıcılar");
        }

        public void refresh() {
            try {
                List<UserSummary> list = api.listUsers();
                table.removeAll();
                for (UserSummary u : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {nz(u.username), nz(u.realName), nz(u.description)});
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
            parent.setLayout(new GridLayout(1, false));
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Para Birimleri");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            SwtForms.primaryAddButton(actions, "Yeni Para Birimi", this::openNewDialog);

            table = SwtForms.makeTable(parent,
                    new String[] {"Ad", "Kısaltma", "Ülke", "Varsayılan", "Sabit"},
                    new int[] {220, 120, 180, 120, 100});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "para_birimleri", "Para Birimleri");
        }

        private void openNewDialog() {
            Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Para Birimi", 460, 340);
            Text name = SwtForms.textRow(dlg, "Ad");
            Text abbr = SwtForms.textRow(dlg, "Kısaltma");
            Text country = SwtForms.textRow(dlg, "Ülke");
            Combo def = SwtForms.comboRow(dlg, "Varsayılan", new String[] {"Hayır", "Evet"});
            Combo constant = SwtForms.comboRow(dlg, "Sabit", new String[] {"Hayır", "Evet"});
            SwtForms.dialogButtonBar(dlg, () -> {
                CreateCurrency r = new CreateCurrency();
                r.name = name.getText().trim();
                r.abbreviation = abbr.getText().trim();
                r.country = country.getText().trim();
                r.defaultCurrency = def.getSelectionIndex() == 1;
                r.constant = constant.getSelectionIndex() == 1;
                try { api.createCurrency(r); refresh(); return true; }
                catch (ApiException ex) { SwtForms.error(dlg, "Hata", ex.getMessage()); return false; }
            });
            SwtForms.runModal(dlg);
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
            parent.setLayout(new GridLayout(1, false));
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Döviz Kurları");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            SwtForms.primaryAddButton(actions, "Yeni Kur", this::openNewDialog);

            table = SwtForms.makeTable(parent,
                    new String[] {"Tarih", "Baz", "Hedef", "Kur"},
                    new int[] {140, 300, 300, 160});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "doviz_kurlari", "Döviz Kurları");
        }

        private void openNewDialog() {
            Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Döviz Kuru", 460, 300);
            Text base = SwtForms.textRow(dlg, "Baz Para UUID");
            Text target = SwtForms.textRow(dlg, "Hedef Para UUID");
            Text ratio = SwtForms.textRow(dlg, "Kur");
            Text date = SwtForms.textRow(dlg, "Tarih (YYYY-MM-DD)");
            date.setText(LocalDate.now().toString());
            SwtForms.dialogButtonBar(dlg, () -> {
                CreateExchangeRate r = new CreateExchangeRate();
                r.baseCurrencyId = base.getText().trim();
                r.exchangeCurrencyId = target.getText().trim();
                r.exchangeRatio = SwtForms.parseDecimal(ratio.getText());
                try { r.date = LocalDate.parse(date.getText().trim()); }
                catch (Exception ex) { SwtForms.error(dlg, "Tarih", "Geçersiz"); return false; }
                try { api.createExchangeRate(r); refresh(); return true; }
                catch (ApiException ex) { SwtForms.error(dlg, "Hata", ex.getMessage()); return false; }
            });
            SwtForms.runModal(dlg);
        }

        public void refresh() {
            try {
                List<ExchangeRateDto> list = api.listExchangeRates();
                table.removeAll();
                for (ExchangeRateDto r : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            r.date == null ? "" : r.date.toString(),
                            nz(r.baseCurrencyId), nz(r.exchangeCurrencyId),
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
            dlg.setSize(420, 240);
            org.eclipse.swt.graphics.Rectangle p = parent.getBounds();
            dlg.setLocation(p.x + (p.width - 420) / 2, p.y + (p.height - 240) / 2);

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
