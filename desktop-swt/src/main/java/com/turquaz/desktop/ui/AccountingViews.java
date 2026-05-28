/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AccountingApi;
import com.turquaz.desktop.rest.AccountingApi.AccountBalance;
import com.turquaz.desktop.rest.AccountingApi.JournalLineRequest;
import com.turquaz.desktop.rest.AccountingApi.PostJournalRequest;
import com.turquaz.desktop.rest.AccountingApi.TrialBalanceLine;
import com.turquaz.desktop.rest.ApiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public final class AccountingViews {

    private AccountingViews() {}

    /** Yevmiye fişi postlama. Çoklu satır + Σborç=Σalacak gösterici. */
    public static class JournalEntryView {
        private final Composite parent;
        private final AccountingApi api;

        public JournalEntryView(Composite parent, AccountingApi api) {
            this.parent = parent;
            this.api = api;
            build();
        }

        private void build() {
            parent.setLayout(new GridLayout(1, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Yevmiye Fişi");

            Group header = new Group(parent, SWT.NONE);
            header.setText("Fiş");
            header.setLayout(new GridLayout(4, false));
            header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Text date = SwtForms.textRow(header, "Tarih (YYYY-MM-DD)");
            Text docNo = SwtForms.textRow(header, "Belge No");
            Text desc = SwtForms.textRow(header, "Açıklama");
            date.setText(LocalDate.now().toString());

            Group ctxGroup = new Group(parent, SWT.NONE);
            ctxGroup.setText("Yevmiye Bağlamı (UUID'ler)");
            ctxGroup.setLayout(new GridLayout(4, false));
            ctxGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Text journalId = SwtForms.textRow(ctxGroup, "Journal");
            Text txTypeId = SwtForms.textRow(ctxGroup, "İşlem Türü");
            Text moduleId = SwtForms.textRow(ctxGroup, "Modül");
            Text seqId = SwtForms.textRow(ctxGroup, "Sıra");
            Text rateId = SwtForms.textRow(ctxGroup, "Kur Kaydı");
            Text rate = SwtForms.textRow(ctxGroup, "Kur");
            rate.setText("1");

            Group linesGroup = new Group(parent, SWT.NONE);
            linesGroup.setText("Satırlar");
            linesGroup.setLayout(new GridLayout(1, false));
            linesGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            Table table = new Table(linesGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            for (String col : new String[] {"Hesap UUID", "Borç", "Alacak", "Açıklama"}) {
                org.eclipse.swt.widgets.TableColumn c = new org.eclipse.swt.widgets.TableColumn(table, SWT.NONE);
                c.setText(col);
                c.setWidth(col.equals("Hesap UUID") ? 280 : col.equals("Açıklama") ? 220 : 100);
            }

            Composite addLineRow = new Composite(linesGroup, SWT.NONE);
            addLineRow.setLayout(new GridLayout(5, false));
            addLineRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Text aId = new Text(addLineRow, SWT.BORDER);
            GridData gd1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd1.widthHint = 260;
            aId.setLayoutData(gd1);
            aId.setMessage("hesap UUID");
            Text aDebit = new Text(addLineRow, SWT.BORDER);
            aDebit.setMessage("borç");
            GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, false, false);
            gd2.widthHint = 100;
            aDebit.setLayoutData(gd2);
            Text aCredit = new Text(addLineRow, SWT.BORDER);
            aCredit.setMessage("alacak");
            aCredit.setLayoutData(gd2);
            Text aDesc = new Text(addLineRow, SWT.BORDER);
            aDesc.setMessage("açıklama");
            GridData gd3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd3.widthHint = 180;
            aDesc.setLayoutData(gd3);
            Button addBtn = new Button(addLineRow, SWT.PUSH);
            addBtn.setText("+ Satır");

            Label totals = new Label(linesGroup, SWT.NONE);
            totals.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Runnable recalc = () -> {
                BigDecimal td = BigDecimal.ZERO, tc = BigDecimal.ZERO;
                for (TableItem it : table.getItems()) {
                    td = td.add(SwtForms.parseDecimal(it.getText(1)));
                    tc = tc.add(SwtForms.parseDecimal(it.getText(2)));
                }
                boolean balanced = td.compareTo(tc) == 0 && td.signum() > 0;
                totals.setText("Σ Borç=" + td.toPlainString() + "  Σ Alacak=" + tc.toPlainString()
                        + (balanced ? "  ✓ dengeli" : "  ⚠ dengesiz"));
            };

            addBtn.addListener(SWT.Selection, e -> {
                if (aId.getText().isBlank()) return;
                TableItem it = new TableItem(table, SWT.NONE);
                it.setText(new String[] {
                        aId.getText().trim(),
                        aDebit.getText().isBlank() ? "0" : aDebit.getText().trim(),
                        aCredit.getText().isBlank() ? "0" : aCredit.getText().trim(),
                        aDesc.getText()
                });
                aId.setText(""); aDebit.setText(""); aCredit.setText(""); aDesc.setText("");
                recalc.run();
            });

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new GridLayout(3, false));
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
            Button removeBtn = new Button(actions, SWT.PUSH);
            removeBtn.setText("Seçili Sil");
            removeBtn.addListener(SWT.Selection, e -> {
                table.remove(table.getSelectionIndices());
                recalc.run();
            });
            Button postBtn = new Button(actions, SWT.PUSH);
            postBtn.setText("Fişi Postla");

            postBtn.addListener(SWT.Selection, e -> {
                PostJournalRequest req = new PostJournalRequest();
                try { req.transactionDate = LocalDate.parse(date.getText().trim()); }
                catch (Exception ex) {
                    SwtForms.error(parent.getShell(), "Tarih", "Geçersiz tarih"); return;
                }
                req.documentNo = docNo.getText().trim();
                req.description = desc.getText();
                req.journalId = journalId.getText().trim();
                req.transactionTypeId = txTypeId.getText().trim();
                req.moduleId = moduleId.getText().trim();
                req.engineSequenceId = seqId.getText().trim();
                req.exchangeRateId = rateId.getText().trim();
                req.exchangeRate = SwtForms.parseDecimal(rate.getText());
                req.lines = new ArrayList<>();
                for (TableItem it : table.getItems()) {
                    JournalLineRequest l = new JournalLineRequest();
                    l.accountId = it.getText(0);
                    l.debit = SwtForms.parseDecimal(it.getText(1));
                    l.credit = SwtForms.parseDecimal(it.getText(2));
                    l.description = it.getText(3);
                    req.lines.add(l);
                }
                try {
                    var res = api.postJournal(req);
                    SwtForms.info(parent.getShell(), "Postlandı",
                            "Fiş kaydedildi: " + res.transactionId);
                    table.removeAll();
                    recalc.run();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            recalc.run();
        }
    }

    /** Mizan tablosu. */
    public static class TrialBalanceView {
        private final Composite parent;
        private final AccountingApi api;
        private Table table;

        public TrialBalanceView(Composite parent, AccountingApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(1, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Mizan");

            Composite top = new Composite(parent, SWT.NONE);
            top.setLayout(new GridLayout(2, false));
            top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            Button refresh = new Button(top, SWT.PUSH);
            refresh.setText("Yenile");
            refresh.addListener(SWT.Selection, e -> refresh());

            table = SwtForms.makeTable(parent,
                    new String[] {"Hesap UUID", "Toplam Borç", "Toplam Alacak", "Bakiye"},
                    new int[] {320, 160, 160, 160});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }

        public void refresh() {
            try {
                List<TrialBalanceLine> list = api.trialBalance(null);
                table.removeAll();
                BigDecimal td = BigDecimal.ZERO, tc = BigDecimal.ZERO;
                for (TrialBalanceLine l : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            l.accountId,
                            l.totalDebit.toPlainString(),
                            l.totalCredit.toPlainString(),
                            l.net.toPlainString()
                    });
                    td = td.add(l.totalDebit);
                    tc = tc.add(l.totalCredit);
                }
                TableItem sum = new TableItem(table, SWT.NONE);
                sum.setText(new String[] {
                        "Σ",
                        td.toPlainString(),
                        tc.toPlainString(),
                        td.subtract(tc).toPlainString()
                });
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Mizan", ex.getMessage());
            }
        }
    }

    /** Tek hesap bakiyesi sorgusu. */
    public static class AccountBalanceView {
        private final Composite parent;
        private final AccountingApi api;

        public AccountBalanceView(Composite parent, AccountingApi api) {
            this.parent = parent;
            this.api = api;
            build();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Hesap Bakiyesi");
            h.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
            Text accountId = SwtForms.textRow(parent, "Hesap UUID");
            Label result = new Label(parent, SWT.NONE);
            result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
            Button q = new Button(parent, SWT.PUSH);
            q.setText("Sorgula");
            q.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            q.addListener(SWT.Selection, e -> {
                try {
                    AccountBalance b = api.accountBalance(accountId.getText().trim(), null);
                    result.setText(String.format(
                            "Borç=%s  Alacak=%s  Bakiye=%s",
                            b.totalDebit.toPlainString(),
                            b.totalCredit.toPlainString(),
                            b.net.toPlainString()));
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });
            // Yardımcı: kullanılmayan import’u bertaraf etmek için (Arrays).
            Arrays.asList();
        }
    }
}
