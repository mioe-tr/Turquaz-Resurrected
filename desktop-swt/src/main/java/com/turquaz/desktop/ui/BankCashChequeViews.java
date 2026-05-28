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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
            parent.setLayout(new GridLayout(1, false));
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Banka Hesapları");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            SwtForms.primaryAddButton(actions, "Yeni Banka Hesabı", this::openNewDialog);

            table = SwtForms.makeTable(parent,
                    new String[] {"Kod", "Banka", "Şube", "Hesap No"},
                    new int[] {120, 220, 180, 220});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "banka_hesaplari", "Banka Hesapları");
        }

        private void openNewDialog() {
            Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Banka Hesabı", 480, 420);
            Text code = SwtForms.textRow(dlg, "Kod");
            Text bank = SwtForms.textRow(dlg, "Banka Adı");
            Text branch = SwtForms.textRow(dlg, "Şube");
            Text accNo = SwtForms.textRow(dlg, "Hesap No");
            Text def = SwtForms.textRow(dlg, "Açıklama");
            Text curId = SwtForms.textRow(dlg, "Para Birimi UUID");
            SwtForms.dialogButtonBar(dlg, () -> {
                CreateBankCard r = new CreateBankCard();
                r.code = code.getText().trim();
                r.bankName = bank.getText().trim();
                r.branchName = branch.getText().trim();
                r.accountNo = accNo.getText().trim();
                r.definition = def.getText();
                r.currencyId = curId.getText().trim();
                try { api.createBankCard(r); refresh(); return true; }
                catch (ApiException ex) { SwtForms.error(dlg, "Hata", ex.getMessage()); return false; }
            });
            SwtForms.runModal(dlg);
        }

        public void refresh() {
            try {
                List<BankCard> list = api.listBankCards();
                table.removeAll();
                for (BankCard c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {nz(c.code), nz(c.bankName), nz(c.branchName), nz(c.accountNo)});
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
            parent.setLayout(new GridLayout(1, false));
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Kasa Kartları");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            SwtForms.primaryAddButton(actions, "Yeni Kasa Kartı", this::openNewDialog);

            table = SwtForms.makeTable(parent,
                    new String[] {"Ad", "Açıklama", "Muhasebe Hesabı"},
                    new int[] {220, 320, 320});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "kasa_kartlari", "Kasa Kartları");
        }

        private void openNewDialog() {
            Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Kasa Kartı", 460, 280);
            Text name = SwtForms.textRow(dlg, "Ad");
            Text def = SwtForms.textRow(dlg, "Açıklama");
            Text acc = SwtForms.textRow(dlg, "Muhasebe Hesabı UUID");
            SwtForms.dialogButtonBar(dlg, () -> {
                CreateCashCard r = new CreateCashCard();
                r.name = name.getText().trim();
                r.definition = def.getText();
                r.accountingAccountsId = acc.getText().trim();
                try { api.createCashCard(r); refresh(); return true; }
                catch (ApiException ex) { SwtForms.error(dlg, "Hata", ex.getMessage()); return false; }
            });
            SwtForms.runModal(dlg);
        }

        public void refresh() {
            try {
                List<CashCard> list = api.listCashCards();
                table.removeAll();
                for (CashCard c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {nz(c.name), nz(c.definition), nz(c.accountingAccountsId)});
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
            parent.setLayout(new GridLayout(1, false));
            Composite header = new Composite(parent, SWT.NONE);
            header.setLayout(new GridLayout(2, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Label title = new Label(header, SWT.NONE);
            title.setText("Çek / Senet");
            title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Composite actions = new Composite(header, SWT.NONE);
            actions.setLayout(new org.eclipse.swt.layout.RowLayout());
            SwtForms.primaryAddButton(actions, "Yeni Çek/Senet", this::openNewDialog);

            Composite topRow = new Composite(parent, SWT.NONE);
            topRow.setLayout(new GridLayout(3, false));
            topRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            new Label(topRow, SWT.NONE).setText("Filtre:");
            filterCombo = new Combo(topRow, SWT.READ_ONLY);
            filterCombo.setItems("Tümü", "Alınan", "Verilen");
            filterCombo.select(0);
            filterCombo.addListener(SWT.Selection, e -> refresh());

            table = SwtForms.makeTable(parent,
                    new String[] {"Çek No", "Borçlu", "Tutar", "Vade", "Tür"},
                    new int[] {140, 280, 140, 140, 120});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "cek_senet", "Çek/Senet");
        }

        private void openNewDialog() {
            Shell dlg = SwtForms.modalShell(parent.getShell(), "Yeni Çek/Senet", 560, 720);
            Text chNo = SwtForms.textRow(dlg, "Çek No");
            Text prtNo = SwtForms.textRow(dlg, "Portföy No");
            Text bankId = SwtForms.textRow(dlg, "Banka UUID");
            Text curId = SwtForms.textRow(dlg, "Para Birimi UUID");
            Text rateId = SwtForms.textRow(dlg, "Kur UUID");
            Text rate = SwtForms.textRow(dlg, "Kur");
            Text bankName = SwtForms.textRow(dlg, "Banka Adı");
            Text branch = SwtForms.textRow(dlg, "Şube");
            Text accNo = SwtForms.textRow(dlg, "Hesap No");
            Text amount = SwtForms.textRow(dlg, "Tutar");
            Text debtor = SwtForms.textRow(dlg, "Borçlu");
            Text payPlace = SwtForms.textRow(dlg, "Ödeme Yeri");
            Text dueDate = SwtForms.textRow(dlg, "Vade (YYYY-MM-DD)");
            Text valueDate = SwtForms.textRow(dlg, "Keşide (YYYY-MM-DD)");
            Combo type = SwtForms.comboRow(dlg, "Tür", new String[] {"Alınan", "Verilen"});
            rate.setText("1");

            SwtForms.dialogButtonBar(dlg, () -> {
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
                catch (Exception ex) { SwtForms.error(dlg, "Vade", "Geçersiz tarih"); return false; }
                try { r.valueDate = LocalDate.parse(valueDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(dlg, "Keşide", "Geçersiz tarih"); return false; }
                r.type = type.getSelectionIndex() + 1;
                try { api.createCheque(r); refresh(); return true; }
                catch (ApiException ex) { SwtForms.error(dlg, "Hata", ex.getMessage()); return false; }
            });
            SwtForms.runModal(dlg);
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
                            nz(c.chequeNo), nz(c.debtor),
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
