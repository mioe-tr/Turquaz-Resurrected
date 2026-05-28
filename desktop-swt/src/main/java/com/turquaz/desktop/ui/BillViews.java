/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.ApiException;
import com.turquaz.desktop.rest.BillApi;
import com.turquaz.desktop.rest.BillApi.Bill;
import com.turquaz.desktop.rest.BillApi.Consignment;
import com.turquaz.desktop.rest.BillApi.CreateBill;
import com.turquaz.desktop.rest.BillApi.CreateConsignment;
import com.turquaz.desktop.rest.BillApi.CreateOrder;
import com.turquaz.desktop.rest.BillApi.InventoryProfitRow;
import com.turquaz.desktop.rest.BillApi.Order;
import java.math.BigDecimal;
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

public final class BillViews {

    private BillViews() {}

    public static class BillsView {
        private final Composite parent;
        private final BillApi api;
        private Table table;

        public BillsView(Composite parent, BillApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Faturalar");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Belge No", "Tür", "Tarih", "Vade", "Yazdı", "Açık"},
                    new int[] {140, 80, 110, 110, 60, 60});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Fatura");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Combo type = SwtForms.comboRow(form, "Tür", new String[] {"Satış", "Alış"});
            Text bDate = SwtForms.textRow(form, "Fatura Tarihi");
            Text dueDate = SwtForms.textRow(form, "Vade");
            Text docNo = SwtForms.textRow(form, "Belge No");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text ccId = SwtForms.textRow(form, "Cari Kart UUID");
            Text rateId = SwtForms.textRow(form, "Kur UUID");
            Text seqId = SwtForms.textRow(form, "Sıra UUID");
            bDate.setText(LocalDate.now().toString());
            dueDate.setText(LocalDate.now().plusDays(30).toString());

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateBill r = new CreateBill();
                r.type = type.getSelectionIndex() + 1;
                try { r.billDate = LocalDate.parse(bDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Tarih", "Geçersiz"); return; }
                try { r.dueDate = LocalDate.parse(dueDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Vade", "Geçersiz"); return; }
                r.documentNo = docNo.getText().trim();
                r.definition = def.getText().trim();
                r.currentCardId = ccId.getText().trim();
                r.exchangeRateId = rateId.getText().trim();
                r.engineSequenceId = seqId.getText().trim();
                try {
                    api.createBill(r);
                    docNo.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new GridLayout(3, false));
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            Button refresh = new Button(actions, SWT.PUSH);
            refresh.setText("Yenile");
            refresh.addListener(SWT.Selection, e -> refresh());
            Button printBtn = new Button(actions, SWT.PUSH);
            printBtn.setText("Seçiliyi Yazdır");
            Button closeBtn = new Button(actions, SWT.PUSH);
            closeBtn.setText("Seçiliyi Kapat");

            printBtn.addListener(SWT.Selection, e -> {
                String id = (String) selected();
                if (id == null) return;
                try { api.printBill(id); refresh(); }
                catch (ApiException ex) { SwtForms.error(parent.getShell(), "Hata", ex.getMessage()); }
            });
            closeBtn.addListener(SWT.Selection, e -> {
                String id = (String) selected();
                if (id == null) return;
                try { api.closeBill(id); refresh(); }
                catch (ApiException ex) { SwtForms.error(parent.getShell(), "Hata", ex.getMessage()); }
            });
        }

        private Object selected() {
            int i = table.getSelectionIndex();
            if (i < 0) return null;
            return table.getItem(i).getData();
        }

        public void refresh() {
            try {
                List<Bill> list = api.listBills();
                table.removeAll();
                for (Bill b : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setData(b.id);
                    it.setText(new String[] {
                            nz(b.documentNo),
                            b.type == null ? "" : (b.type == 1 ? "Satış" : "Alış"),
                            b.billDate == null ? "" : b.billDate.toString(),
                            b.dueDate == null ? "" : b.dueDate.toString(),
                            Boolean.TRUE.equals(b.printed) ? "✓" : "—",
                            Boolean.TRUE.equals(b.open) ? "✓" : "—"
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    public static class OrdersView {
        private final Composite parent;
        private final BillApi api;
        private Table table;

        public OrdersView(Composite parent, BillApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Siparişler");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Belge No", "Tür", "Tarih", "Teslim", "Tutar", "Teslim Edildi"},
                    new int[] {120, 80, 110, 110, 140, 110});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Sipariş");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Combo type = SwtForms.comboRow(form, "Tür", new String[] {"Satış", "Alış"});
            Text docNo = SwtForms.textRow(form, "Belge No (sayı)");
            Text oDate = SwtForms.textRow(form, "Sipariş Tarihi");
            Text dDate = SwtForms.textRow(form, "Vade");
            Text dlvDate = SwtForms.textRow(form, "Teslim Tarihi");
            Text ccId = SwtForms.textRow(form, "Cari UUID");
            Text billId = SwtForms.textRow(form, "Fatura UUID");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text disc = SwtForms.textRow(form, "İskonto %");
            Text vat = SwtForms.textRow(form, "KDV %");
            Text discAmt = SwtForms.textRow(form, "İskonto Tutarı");
            Text charges = SwtForms.textRow(form, "Masraflar");
            Text vatAmt = SwtForms.textRow(form, "KDV Tutarı");
            Text total = SwtForms.textRow(form, "Toplam Tutar");
            oDate.setText(LocalDate.now().toString());
            dDate.setText(LocalDate.now().plusDays(30).toString());
            dlvDate.setText(LocalDate.now().plusDays(7).toString());
            disc.setText("0"); vat.setText("18");
            discAmt.setText("0"); charges.setText("0"); vatAmt.setText("0"); total.setText("0");

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateOrder r = new CreateOrder();
                r.type = type.getSelectionIndex() + 1;
                r.documentNo = SwtForms.parseInt(docNo.getText(), 0);
                try { r.orderDate = LocalDate.parse(oDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Tarih", "Geçersiz"); return; }
                try { r.dueDate = LocalDate.parse(dDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Vade", "Geçersiz"); return; }
                try { r.deliverDate = LocalDate.parse(dlvDate.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Teslim", "Geçersiz"); return; }
                r.currentCardId = ccId.getText().trim();
                r.billId = billId.getText().trim();
                r.definition = def.getText().trim();
                r.discountRatePercent = SwtForms.parseInt(disc.getText(), 0);
                r.vatPercent = SwtForms.parseInt(vat.getText(), 0);
                r.discountAmount = SwtForms.parseDecimal(discAmt.getText());
                r.charges = SwtForms.parseDecimal(charges.getText());
                r.vatAmount = SwtForms.parseDecimal(vatAmt.getText());
                r.totalAmount = SwtForms.parseDecimal(total.getText());
                try {
                    api.createOrder(r);
                    docNo.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new GridLayout(2, false));
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            Button refresh = new Button(actions, SWT.PUSH);
            refresh.setText("Yenile");
            refresh.addListener(SWT.Selection, e -> refresh());
            Button deliver = new Button(actions, SWT.PUSH);
            deliver.setText("Seçiliyi Teslim Et");
            deliver.addListener(SWT.Selection, e -> {
                int i = table.getSelectionIndex();
                if (i < 0) return;
                String id = (String) table.getItem(i).getData();
                try { api.deliverOrder(id); refresh(); }
                catch (ApiException ex) { SwtForms.error(parent.getShell(), "Hata", ex.getMessage()); }
            });
        }

        public void refresh() {
            try {
                List<Order> list = api.listOrders();
                table.removeAll();
                for (Order o : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setData(o.id);
                    it.setText(new String[] {
                            o.documentNo == null ? "" : o.documentNo.toString(),
                            o.type == null ? "" : (o.type == 1 ? "Satış" : "Alış"),
                            o.orderDate == null ? "" : o.orderDate.toString(),
                            o.deliverDate == null ? "" : o.deliverDate.toString(),
                            o.totalAmount == null ? "" : o.totalAmount.toPlainString(),
                            Boolean.TRUE.equals(o.delivered) ? "✓" : "—"
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    public static class ConsignmentsView {
        private final Composite parent;
        private final BillApi api;
        private Table table;

        public ConsignmentsView(Composite parent, BillApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            new Label(parent, SWT.NONE).setText("Konsinye");
            new Label(parent, SWT.NONE);

            table = SwtForms.makeTable(parent,
                    new String[] {"Belge No", "Tür", "Tarih", "Ref. Fatura", "Yazdırıldı"},
                    new int[] {140, 80, 110, 140, 100});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Konsinye");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Combo type = SwtForms.comboRow(form, "Tür", new String[] {"Verilen", "Alınan"});
            Text date = SwtForms.textRow(form, "Tarih");
            Text docNo = SwtForms.textRow(form, "Belge No");
            Text refBill = SwtForms.textRow(form, "Ref. Fatura No");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text ccId = SwtForms.textRow(form, "Cari UUID");
            Text rateId = SwtForms.textRow(form, "Kur UUID");
            Text seqId = SwtForms.textRow(form, "Sıra UUID");
            date.setText(LocalDate.now().toString());

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateConsignment r = new CreateConsignment();
                r.type = type.getSelectionIndex() + 1;
                try { r.date = LocalDate.parse(date.getText().trim()); }
                catch (Exception ex) { SwtForms.error(parent.getShell(), "Tarih", "Geçersiz"); return; }
                r.documentNo = docNo.getText().trim();
                r.referenceBillNo = refBill.getText().trim();
                r.definition = def.getText().trim();
                r.currentCardId = ccId.getText().trim();
                r.exchangeRateId = rateId.getText().trim();
                r.engineSequenceId = seqId.getText().trim();
                try {
                    api.createConsignment(r);
                    docNo.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite actions = new Composite(parent, SWT.NONE);
            actions.setLayout(new GridLayout(2, false));
            actions.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            Button refresh = new Button(actions, SWT.PUSH);
            refresh.setText("Yenile");
            refresh.addListener(SWT.Selection, e -> refresh());
            Button print = new Button(actions, SWT.PUSH);
            print.setText("Seçiliyi Yazdır");
            print.addListener(SWT.Selection, e -> {
                int i = table.getSelectionIndex();
                if (i < 0) return;
                String id = (String) table.getItem(i).getData();
                try { api.printConsignment(id); refresh(); }
                catch (ApiException ex) { SwtForms.error(parent.getShell(), "Hata", ex.getMessage()); }
            });
        }

        public void refresh() {
            try {
                List<Consignment> list = api.listConsignments();
                table.removeAll();
                for (Consignment c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setData(c.id);
                    it.setText(new String[] {
                            nz(c.documentNo),
                            c.type == null ? "" : (c.type == 1 ? "Verilen" : "Alınan"),
                            c.date == null ? "" : c.date.toString(),
                            nz(c.referenceBillNo),
                            Boolean.TRUE.equals(c.printed) ? "✓" : "—"
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste", ex.getMessage());
            }
        }
    }

    public static class InventoryProfitView {
        private final Composite parent;
        private final BillApi api;
        private Table table;
        private Text fromTx;
        private Text toTx;
        private Label totals;

        public InventoryProfitView(Composite parent, BillApi api) {
            this.parent = parent;
            this.api = api;
            build();
        }

        private void build() {
            parent.setLayout(new GridLayout(1, false));
            new Label(parent, SWT.NONE).setText("Stok Kâr Analizi");

            Composite top = new Composite(parent, SWT.NONE);
            top.setLayout(new GridLayout(5, false));
            top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            new Label(top, SWT.NONE).setText("Başlangıç (YYYY-MM-DD):");
            fromTx = new Text(top, SWT.BORDER);
            GridData gd = new GridData();
            gd.widthHint = 120;
            fromTx.setLayoutData(gd);
            fromTx.setText(LocalDate.now().minusYears(1).toString());
            new Label(top, SWT.NONE).setText("Bitiş:");
            toTx = new Text(top, SWT.BORDER);
            toTx.setLayoutData(gd);
            toTx.setText(LocalDate.now().toString());
            Button run = new Button(top, SWT.PUSH);
            run.setText("Hesapla");
            run.addListener(SWT.Selection, e -> refresh());

            table = SwtForms.makeTable(parent,
                    new String[] {"Kart UUID", "Giriş", "Çıkış", "Maliyet", "Gelir",
                            "Ort. Birim", "Satılanın Maliyeti", "Kâr"},
                    new int[] {240, 90, 90, 110, 110, 110, 140, 110});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            totals = new Label(parent, SWT.NONE);
            totals.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        public void refresh() {
            String from = fromTx.getText().trim() + "T00:00:00Z";
            String to = toTx.getText().trim() + "T23:59:59Z";
            try {
                List<InventoryProfitRow> rows = api.inventoryProfit(from, to);
                table.removeAll();
                BigDecimal totalProfit = BigDecimal.ZERO;
                BigDecimal totalRevenue = BigDecimal.ZERO;
                for (InventoryProfitRow r : rows) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            r.cardId,
                            d(r.amountIn), d(r.amountOut),
                            d(r.costIn), d(r.revenueOut),
                            d(r.avgUnitCost), d(r.costOfSold),
                            d(r.profit)
                    });
                    if (r.profit != null) totalProfit = totalProfit.add(r.profit);
                    if (r.revenueOut != null) totalRevenue = totalRevenue.add(r.revenueOut);
                }
                totals.setText("Σ Gelir=" + totalRevenue.toPlainString()
                        + "  Σ Kâr=" + totalProfit.toPlainString());
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Rapor", ex.getMessage());
            }
        }

        private static String d(BigDecimal b) { return b == null ? "" : b.toPlainString(); }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
