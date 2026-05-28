/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.ApiException;
import com.turquaz.desktop.rest.BankCashChequeApi;
import com.turquaz.desktop.rest.BankCashChequeApi.BankCard;
import com.turquaz.desktop.rest.BankCashChequeApi.CashCard;
import com.turquaz.desktop.rest.BankCashChequeApi.Cheque;
import com.turquaz.desktop.rest.BankCashChequeApi.CreateBankCard;
import com.turquaz.desktop.rest.BankCashChequeApi.CreateCashCard;
import com.turquaz.desktop.rest.BankCashChequeApi.CreateCheque;
import java.time.LocalDate;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public final class BankCashChequeViews {

    private BankCashChequeViews() {}

    public static class BankCardsView {
        private final Composite parent;
        private final BankCashChequeApi api;
        private Table table;

        public BankCardsView(Composite parent, BankCashChequeApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Banka Hesapları");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Kod", "Banka", "Şube", "Hesap No"},
                    new int[] {100, 180, 140, 160});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Banka Hesabı");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text code = SwtForms.textRow(form, "Kod");
            Text bank = SwtForms.textRow(form, "Banka Adı");
            Text branch = SwtForms.textRow(form, "Şube");
            Text accNo = SwtForms.textRow(form, "Hesap No");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text curId = SwtForms.textRow(form, "Para Birimi UUID");

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateBankCard r = new CreateBankCard();
                r.code = code.getText().trim();
                r.bankName = bank.getText().trim();
                r.branchName = branch.getText().trim();
                r.accountNo = accNo.getText().trim();
                r.definition = def.getText();
                r.currencyId = curId.getText().trim();
                try {
                    api.createBankCard(r);
                    code.setText(""); bank.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "banka_hesaplari", "Banka Hesapları");
        }

        public void refresh() {
            try {
                List<BankCard> list = api.listBankCards();
                table.removeAll();
                for (BankCard c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(c.code), nz(c.bankName), nz(c.branchName), nz(c.accountNo)
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    public static class CashCardsView {
        private final Composite parent;
        private final BankCashChequeApi api;
        private Table table;

        public CashCardsView(Composite parent, BankCashChequeApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Kasa Kartları");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Ad", "Açıklama", "Muhasebe Hesabı"},
                    new int[] {200, 280, 320});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Kasa Kartı");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text name = SwtForms.textRow(form, "Ad");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text acc = SwtForms.textRow(form, "Muhasebe Hesabı UUID");
            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateCashCard r = new CreateCashCard();
                r.name = name.getText().trim();
                r.definition = def.getText();
                r.accountingAccountsId = acc.getText().trim();
                try {
                    api.createCashCard(r);
                    name.setText(""); def.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "kasa_kartlari", "Kasa Kartları");
        }

        public void refresh() {
            try {
                List<CashCard> list = api.listCashCards();
                table.removeAll();
                for (CashCard c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(c.name), nz(c.definition), nz(c.accountingAccountsId)
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    public static class ChequesView {
        private final Composite parent;
        private final BankCashChequeApi api;
        private Table table;
        private Combo filterCombo;

        public ChequesView(Composite parent, BankCashChequeApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Çek/Senet");
            new Label(parent, SWT.NONE);

            Composite topRow = new Composite(parent, SWT.NONE);
            topRow.setLayout(new GridLayout(3, false));
            topRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
            new Label(topRow, SWT.NONE).setText("Filtre:");
            filterCombo = new Combo(topRow, SWT.READ_ONLY);
            filterCombo.setItems("Tümü", "Alınan", "Verilen");
            filterCombo.select(0);
            filterCombo.addListener(SWT.Selection, e -> refresh());
            SwtForms.refreshButton(topRow, this::refresh);

            table = SwtForms.makeTable(parent,
                    new String[] {"Çek No", "Borçlu", "Tutar", "Vade", "Tür"},
                    new int[] {120, 220, 120, 120, 100});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            // Üst satırdaki toolbar'a Excel butonu — table artık tanımlı.
            SwtForms.exportButton(topRow, table, "cek_senet", "Çek/Senet");

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Çek/Senet");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text chNo = SwtForms.textRow(form, "Çek No");
            Text prtNo = SwtForms.textRow(form, "Portföy No");
            Text bankId = SwtForms.textRow(form, "Banka UUID");
            Text curId = SwtForms.textRow(form, "Para Birimi UUID");
            Text rateId = SwtForms.textRow(form, "Kur UUID");
            Text rate = SwtForms.textRow(form, "Kur");
            Text bankName = SwtForms.textRow(form, "Banka Adı");
            Text branch = SwtForms.textRow(form, "Şube");
            Text accNo = SwtForms.textRow(form, "Hesap No");
            Text amount = SwtForms.textRow(form, "Tutar");
            Text debtor = SwtForms.textRow(form, "Borçlu");
            Text payPlace = SwtForms.textRow(form, "Ödeme Yeri");
            Text dueDate = SwtForms.textRow(form, "Vade (YYYY-MM-DD)");
            Text valueDate = SwtForms.textRow(form, "Keşide (YYYY-MM-DD)");
            Combo type = SwtForms.comboRow(form, "Tür", new String[] {"Alınan", "Verilen"});
            rate.setText("1");

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateCheque r = new CreateCheque();
                r.chequeNo = chNo.getText().trim();
                r.portfolioNo = prtNo.getText().trim();
                r.bankId = bankId.getText().trim();
                r.currencyId = curId.getText().trim();
                r.exchangeRateId = rateId.getText().trim();
                r.exchangeRate = SwtForms.parseDecimal(rate.getText());
                r.bankName = bankName.getText().trim();
                r.bankBranchName = branch.getText().trim();
                r.bankAccountNo = accNo.getText().trim();
                r.amount = SwtForms.parseDecimal(amount.getText());
                r.debtor = debtor.getText().trim();
                r.paymentPlace = payPlace.getText();
                try { r.dueDate = LocalDate.parse(dueDate.getText().trim()); }
                catch (Exception ex) {
                    SwtForms.error(parent.getShell(), "Vade", "Geçersiz tarih"); return;
                }
                try { r.valueDate = LocalDate.parse(valueDate.getText().trim()); }
                catch (Exception ex) {
                    SwtForms.error(parent.getShell(), "Keşide", "Geçersiz tarih"); return;
                }
                r.type = type.getSelectionIndex() + 1;
                try {
                    api.createCheque(r);
                    chNo.setText(""); amount.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });
        }

        public void refresh() {
            Integer t = switch (filterCombo.getSelectionIndex()) {
                case 1 -> 1;
                case 2 -> 2;
                default -> null;
            };
            try {
                List<Cheque> list = api.listCheques(t);
                table.removeAll();
                for (Cheque c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(c.chequeNo),
                            nz(c.debtor),
                            c.amount == null ? "" : c.amount.toPlainString(),
                            c.dueDate == null ? "" : c.dueDate.toString(),
                            c.type == null ? "" : (c.type == 1 ? "Alınan" : "Verilen")
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
