/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.ApiException;
import com.turquaz.desktop.rest.InventoryApi;
import com.turquaz.desktop.rest.InventoryApi.CreateCard;
import com.turquaz.desktop.rest.InventoryApi.CreateWarehouse;
import com.turquaz.desktop.rest.InventoryApi.InventoryCard;
import com.turquaz.desktop.rest.InventoryApi.StockOnHand;
import com.turquaz.desktop.rest.InventoryApi.Warehouse;
import java.time.Instant;
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

/** Stok kartları, depolar, anlık stok bakiyesi görünümleri. */
public final class InventoryViews {

    private InventoryViews() {}

    public static class CardsView {
        private final Composite parent;
        private final InventoryApi api;
        private Table table;

        public CardsView(Composite parent, InventoryApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Stok Kartları");
            h.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            table = SwtForms.makeTable(parent,
                    new String[] {"Kod", "Ad", "Min", "Maks", "KDV %"},
                    new int[] {120, 240, 80, 80, 80});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Stok Kartı");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text code = SwtForms.textRow(form, "Kod");
            Text name = SwtForms.textRow(form, "Ad");
            Text def = SwtForms.textRow(form, "Açıklama");
            Text minA = SwtForms.textRow(form, "Min. Miktar");
            Text maxA = SwtForms.textRow(form, "Maks. Miktar");
            Text vat = SwtForms.textRow(form, "KDV %");
            Text disc = SwtForms.textRow(form, "İskonto %");
            Text sVat = SwtForms.textRow(form, "ÖTV %");
            minA.setText("0"); maxA.setText("0"); vat.setText("18");
            disc.setText("0"); sVat.setText("0");

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateCard r = new CreateCard();
                r.code = code.getText().trim();
                r.name = name.getText().trim();
                r.definition = def.getText();
                r.minimumAmount = SwtForms.parseInt(minA.getText(), 0);
                r.maximumAmount = SwtForms.parseInt(maxA.getText(), 0);
                r.vatRate = SwtForms.parseInt(vat.getText(), 0);
                r.discountPercent = SwtForms.parseInt(disc.getText(), 0);
                r.specialVatRate = SwtForms.parseInt(sVat.getText(), 0);
                try {
                    api.createCard(r);
                    code.setText(""); name.setText(""); def.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "stok_kartlari", "Stok Kartları");
        }

        public void refresh() {
            try {
                List<InventoryCard> list = api.listCards();
                table.removeAll();
                for (InventoryCard c : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(c.code), nz(c.name),
                            str(c.minimumAmount), str(c.maximumAmount),
                            str(c.vatRate)
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste alınamadı", ex.getMessage());
            }
        }
    }

    public static class WarehousesView {
        private final Composite parent;
        private final InventoryApi api;
        private Table table;

        public WarehousesView(Composite parent, InventoryApi api) {
            this.parent = parent;
            this.api = api;
            build();
            refresh();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Depolar");
            h.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            table = SwtForms.makeTable(parent,
                    new String[] {"Kod", "Ad", "Şehir", "Telefon"},
                    new int[] {120, 220, 120, 140});
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Group form = new Group(parent, SWT.NONE);
            form.setText("Yeni Depo");
            form.setLayout(new GridLayout(2, false));
            form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
            Text code = SwtForms.textRow(form, "Kod");
            Text name = SwtForms.textRow(form, "Ad");
            Text address = SwtForms.textRow(form, "Adres");
            Text city = SwtForms.textRow(form, "Şehir");
            Text tel = SwtForms.textRow(form, "Telefon");
            Text desc = SwtForms.textRow(form, "Açıklama");

            Button save = new Button(form, SWT.PUSH);
            save.setText("Kaydet");
            save.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            save.addListener(SWT.Selection, e -> {
                CreateWarehouse r = new CreateWarehouse();
                r.code = code.getText().trim();
                r.name = name.getText().trim();
                r.address = address.getText();
                r.city = city.getText();
                r.telephone = tel.getText();
                r.description = desc.getText();
                try {
                    api.createWarehouse(r);
                    code.setText(""); name.setText("");
                    refresh();
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });

            Composite toolbar = new Composite(parent, SWT.NONE);
            toolbar.setLayout(new org.eclipse.swt.layout.RowLayout());
            toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));
            SwtForms.refreshButton(toolbar, this::refresh);
            SwtForms.exportButton(toolbar, table, "depolar", "Depolar");
        }

        public void refresh() {
            try {
                List<Warehouse> list = api.listWarehouses();
                table.removeAll();
                for (Warehouse w : list) {
                    TableItem it = new TableItem(table, SWT.NONE);
                    it.setText(new String[] {
                            nz(w.code), nz(w.name), nz(w.city), nz(w.telephone)
                    });
                }
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Liste alınamadı", ex.getMessage());
            }
        }
    }

    public static class StockOnHandView {
        private final Composite parent;
        private final InventoryApi api;
        private Combo cardCombo;
        private Combo warehouseCombo;
        private List<InventoryCard> cards;
        private List<Warehouse> warehouses;
        private Label result;

        public StockOnHandView(Composite parent, InventoryApi api) {
            this.parent = parent;
            this.api = api;
            build();
        }

        private void build() {
            parent.setLayout(new GridLayout(2, false));
            Label h = new Label(parent, SWT.NONE);
            h.setText("Stok Bakiyesi");
            h.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            try {
                cards = api.listCards();
                warehouses = api.listWarehouses();
            } catch (ApiException ex) {
                SwtForms.error(parent.getShell(), "Yükleme hatası", ex.getMessage());
                cards = List.of();
                warehouses = List.of();
            }

            cardCombo = SwtForms.comboRow(parent, "Stok Kartı",
                    cards.stream().map(c -> c.code + " — " + c.name).toArray(String[]::new));
            warehouseCombo = SwtForms.comboRow(parent, "Depo",
                    warehouses.stream().map(w -> w.code + " — " + w.name).toArray(String[]::new));

            Button calc = new Button(parent, SWT.PUSH);
            calc.setText("Hesapla");
            calc.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));

            result = new Label(parent, SWT.NONE);
            result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

            calc.addListener(SWT.Selection, e -> {
                if (cardCombo.getSelectionIndex() < 0 || warehouseCombo.getSelectionIndex() < 0) return;
                InventoryCard c = cards.get(cardCombo.getSelectionIndex());
                Warehouse w = warehouses.get(warehouseCombo.getSelectionIndex());
                try {
                    StockOnHand soh = api.stockOnHand(c.id, w.id, Instant.now());
                    result.setText("Anlık bakiye: " + soh.amount.toPlainString());
                } catch (ApiException ex) {
                    SwtForms.error(parent.getShell(), "Hata", ex.getMessage());
                }
            });
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
