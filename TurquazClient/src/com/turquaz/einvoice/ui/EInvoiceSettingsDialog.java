package com.turquaz.einvoice.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import server.util.EngBLLogger;

import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.config.EInvoiceSettingsStore;
import com.turquaz.einvoice.config.ProviderProfile;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.einvoice.provider.EInvoiceProvider;
import com.turquaz.einvoice.provider.EInvoiceProviderRegistry;
import com.turquaz.einvoice.provider.ProviderCredentials;

/**
 * e-belge entegrasyon ayarlarý: mükellef (satýcý) kimliði, entegratör
 * profilleri ve e-Arþiv için aktif profil seçimi.
 *
 * <p>Çoklu entegratör desteklenir: birden fazla profil tanýmlanabilir; her
 * profilin kendi entegratörü, kimlik bilgisi ve fatura serisi olur.
 */
public class EInvoiceSettingsDialog extends org.eclipse.swt.widgets.Dialog {

    private Shell dialogShell;
    private EInvoiceSettings settings;

    // satýcý
    private Text txtSellerVkn;
    private Text txtSellerOffice;
    private Text txtSellerCity;
    private Text txtSellerDistrict;

    // profil formu
    private Text txtId;
    private Combo cmbProvider;
    private Combo cmbEnv;
    private Text txtApiKey;
    private Text txtApiSecret;
    private Text txtSenderAlias;
    private Text txtSeries;
    private Table tblProfiles;

    // yönlendirme
    private Combo cmbActiveEarsiv;

    public EInvoiceSettingsDialog(Shell parent, int style) {
        super(parent, style);
    }

    public void open() {
        try {
            settings = EInvoiceSettingsStore.load();

            dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
            dialogShell.setText("e-Belge Entegratör Ayarlarý");
            dialogShell.setLayout(new GridLayout(1, false));
            dialogShell.setSize(600, 640);

            buildSellerGroup();
            buildProfileGroup();
            buildRoutingGroup();
            buildButtons();

            reloadFromSettings();

            dialogShell.open();
            Display display = dialogShell.getDisplay();
            while (!dialogShell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            EngBLLogger.log(this.getClass(), e, getParent());
        }
    }

    private void buildSellerGroup() {
        Group g = new Group(dialogShell, SWT.NONE);
        g.setText("Satýcý (mükellef) kimliði");
        g.setLayout(new GridLayout(4, false));
        g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        txtSellerVkn = labeledText(g, "VKN / TCKN");
        txtSellerOffice = labeledText(g, "Vergi dairesi");
        txtSellerCity = labeledText(g, "Ýl");
        txtSellerDistrict = labeledText(g, "Ýlçe");
    }

    private void buildProfileGroup() {
        Group g = new Group(dialogShell, SWT.NONE);
        g.setText("Entegratör profilleri");
        g.setLayout(new GridLayout(4, false));
        g.setLayoutData(new GridData(GridData.FILL_BOTH));

        txtId = labeledText(g, "Profil adý");

        new Label(g, SWT.NONE).setText("Entegratör");
        cmbProvider = new Combo(g, SWT.READ_ONLY);
        cmbProvider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        List<EInvoiceProvider> providers = EInvoiceProviderRegistry.all();
        for (int i = 0; i < providers.size(); i++) {
            cmbProvider.add(providers.get(i).displayName());
            cmbProvider.setData(providers.get(i).displayName(), providers.get(i).name());
        }
        if (cmbProvider.getItemCount() > 0) {
            cmbProvider.select(0);
        }

        new Label(g, SWT.NONE).setText("Ortam");
        cmbEnv = new Combo(g, SWT.READ_ONLY);
        cmbEnv.add("TEST");
        cmbEnv.add("LIVE");
        cmbEnv.select(0);
        cmbEnv.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        txtApiKey = labeledText(g, "API anahtarý");
        txtApiSecret = labeledText(g, "API secret");
        txtSenderAlias = labeledText(g, "GB-PK etiketi");
        txtSeries = labeledText(g, "e-Arþiv serisi");

        Button btnAdd = new Button(g, SWT.PUSH);
        btnAdd.setText("Ekle / Güncelle");
        btnAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                upsertProfile();
            }
        });

        Button btnDel = new Button(g, SWT.PUSH);
        btnDel.setText("Seçili profili sil");
        btnDel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deleteSelected();
            }
        });
        // hizalama için boþ hücreler
        new Label(g, SWT.NONE);
        new Label(g, SWT.NONE);

        tblProfiles = new Table(g, SWT.BORDER | SWT.FULL_SELECTION);
        tblProfiles.setHeaderVisible(true);
        tblProfiles.setLinesVisible(true);
        GridData td = new GridData(GridData.FILL_BOTH);
        td.horizontalSpan = 4;
        tblProfiles.setLayoutData(td);
        addCol("Profil", 120);
        addCol("Entegratör", 120);
        addCol("Ortam", 80);
        addCol("e-Arþiv serisi", 120);
        tblProfiles.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                loadSelectedIntoForm();
            }
        });
    }

    private void buildRoutingGroup() {
        Group g = new Group(dialogShell, SWT.NONE);
        g.setText("Aktif yönlendirme");
        g.setLayout(new GridLayout(2, false));
        g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(g, SWT.NONE).setText("e-Arþiv için aktif profil");
        cmbActiveEarsiv = new Combo(g, SWT.READ_ONLY);
        cmbActiveEarsiv.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void buildButtons() {
        Group g = new Group(dialogShell, SWT.NONE);
        g.setLayout(new GridLayout(2, false));
        g.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        Button btnSave = new Button(g, SWT.PUSH);
        btnSave.setText("Kaydet");
        btnSave.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                save();
            }
        });

        Button btnClose = new Button(g, SWT.PUSH);
        btnClose.setText("Kapat");
        btnClose.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                dialogShell.dispose();
            }
        });
    }

    // ---- veri <-> form -----------------------------------------------------

    private void reloadFromSettings() {
        setText(txtSellerVkn, settings.getSellerTaxNumber());
        setText(txtSellerOffice, settings.getSellerTaxOffice());
        setText(txtSellerCity, settings.getSellerCity());
        setText(txtSellerDistrict, settings.getSellerDistrict());
        refreshProfileTable();
    }

    private void refreshProfileTable() {
        tblProfiles.removeAll();
        cmbActiveEarsiv.removeAll();
        cmbActiveEarsiv.add("(yok)");
        cmbActiveEarsiv.select(0);
        ProviderProfile active = settings.getActiveProfile(EInvoiceType.EARSIV);
        int idx = 1;
        for (ProviderProfile p : settings.getProfiles()) {
            TableItem it = new TableItem(tblProfiles, SWT.NONE);
            it.setText(new String[] {
                    nv(p.getId()), nv(p.getProviderName()),
                    p.getCredentials().getEnvironment().name(),
                    nv(p.getSeries(EInvoiceType.EARSIV)) });
            it.setData(p.getId());
            cmbActiveEarsiv.add(p.getId());
            if (active != null && active.getId().equals(p.getId())) {
                cmbActiveEarsiv.select(idx);
            }
            idx++;
        }
    }

    private void upsertProfile() {
        String id = txtId.getText().trim();
        if (id.length() == 0) {
            info("Profil adý boþ olamaz.");
            return;
        }
        ProviderProfile p = settings.getProfile(id);
        if (p == null) {
            p = new ProviderProfile(id, selectedProviderName());
            settings.addProfile(p);
        } else {
            p.setProviderName(selectedProviderName());
        }
        ProviderCredentials c = p.getCredentials();
        c.setApiKey(txtApiKey.getText().trim());
        c.setApiSecret(txtApiSecret.getText().trim());
        c.setSenderAlias(txtSenderAlias.getText().trim());
        c.setEnvironment("LIVE".equals(cmbEnv.getText())
                ? ProviderCredentials.Environment.LIVE
                : ProviderCredentials.Environment.TEST);
        p.setSeries(EInvoiceType.EARSIV, txtSeries.getText().trim());
        refreshProfileTable();
    }

    private void deleteSelected() {
        int i = tblProfiles.getSelectionIndex();
        if (i < 0) {
            return;
        }
        String id = (String) tblProfiles.getItem(i).getData();
        settings.removeProfile(id);
        refreshProfileTable();
    }

    private void loadSelectedIntoForm() {
        int i = tblProfiles.getSelectionIndex();
        if (i < 0) {
            return;
        }
        ProviderProfile p = settings.getProfile((String) tblProfiles.getItem(i).getData());
        if (p == null) {
            return;
        }
        setText(txtId, p.getId());
        selectProvider(p.getProviderName());
        cmbEnv.setText(p.getCredentials().getEnvironment().name());
        setText(txtApiKey, p.getCredentials().getApiKey());
        setText(txtApiSecret, p.getCredentials().getApiSecret());
        setText(txtSenderAlias, p.getCredentials().getSenderAlias());
        setText(txtSeries, p.getSeries(EInvoiceType.EARSIV));
    }

    private void save() {
        try {
            settings.setSellerTaxNumber(txtSellerVkn.getText().trim());
            settings.setSellerTaxOffice(txtSellerOffice.getText().trim());
            settings.setSellerCity(txtSellerCity.getText().trim());
            settings.setSellerDistrict(txtSellerDistrict.getText().trim());

            String activeId = cmbActiveEarsiv.getText();
            if (activeId == null || activeId.length() == 0 || "(yok)".equals(activeId)) {
                settings.getRouting().remove(EInvoiceType.EARSIV);
            } else {
                settings.setActiveProfile(EInvoiceType.EARSIV, activeId);
            }

            List<String> errors = settings.validate();
            if (!errors.isEmpty()) {
                StringBuilder b = new StringBuilder("Ayarlar kaydedildi ancak uyarýlar var:\n");
                for (String er : errors) {
                    b.append("- ").append(er).append("\n");
                }
                EInvoiceSettingsStore.save(settings);
                info(b.toString());
                return;
            }
            EInvoiceSettingsStore.save(settings);
            info("Ayarlar kaydedildi.");
        } catch (Exception e) {
            EngBLLogger.log(this.getClass(), e, getParent());
            info("Kaydetme hatasý: " + e.getMessage());
        }
    }

    // ---- küçük yardýmcýlar -------------------------------------------------

    private String selectedProviderName() {
        String disp = cmbProvider.getText();
        Object name = cmbProvider.getData(disp);
        return name != null ? name.toString() : disp;
    }

    private void selectProvider(String name) {
        for (int i = 0; i < cmbProvider.getItemCount(); i++) {
            String disp = cmbProvider.getItem(i);
            Object n = cmbProvider.getData(disp);
            if (n != null && n.equals(name)) {
                cmbProvider.select(i);
                return;
            }
        }
    }

    private Text labeledText(org.eclipse.swt.widgets.Composite parent, String label) {
        new Label(parent, SWT.NONE).setText(label);
        Text t = new Text(parent, SWT.BORDER);
        t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return t;
    }

    private void addCol(String title, int width) {
        TableColumn c = new TableColumn(tblProfiles, SWT.NONE);
        c.setText(title);
        c.setWidth(width);
    }

    private void info(String msg) {
        MessageBox box = new MessageBox(dialogShell, SWT.ICON_INFORMATION | SWT.OK);
        box.setText("e-Belge Ayarlarý");
        box.setMessage(msg);
        box.open();
    }

    private static void setText(Text t, String v) {
        t.setText(v == null ? "" : v);
    }

    private static String nv(String s) {
        return s == null ? "" : s;
    }
}
