package com.turquaz.einvoice.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import server.util.EngBLLogger;

import com.turquaz.einvoice.bl.EinvoiceBLIssue;
import com.turquaz.einvoice.config.EInvoiceSettings;
import com.turquaz.einvoice.config.EInvoiceSettingsStore;
import com.turquaz.einvoice.dal.EinvoiceDAL;
import com.turquaz.einvoice.model.EInvoiceResult;
import com.turquaz.einvoice.model.EInvoiceType;
import com.turquaz.engine.dal.TurqEInvoiceStatus;

/**
 * Seçili bir fatura için e-Arþiv kesme / durum görüntüleme penceresi.
 *
 * <p>Fatura otomatik kesilmez: kullanýcý bu pencerede "e-Arþiv Kes" düðmesine
 * basýnca {@link EinvoiceBLIssue} seçili entegratöre gönderir. Entegratör
 * profilleri {@link EInvoiceSettingsDialog} ile tanýmlanýr.
 */
public class EInvoiceIssueDialog extends org.eclipse.swt.widgets.Dialog {

    private final Integer transactionId;
    private Shell dialogShell;
    private Text txtStatus;

    public EInvoiceIssueDialog(Shell parent, int style, Integer transactionId) {
        super(parent, style);
        this.transactionId = transactionId;
    }

    public void open() {
        try {
            dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
            dialogShell.setText("e-Arþiv Fatura");
            dialogShell.setLayout(new GridLayout(3, false));
            dialogShell.setSize(520, 320);

            Label lbl = new Label(dialogShell, SWT.NONE);
            lbl.setText("Fatura e-belge durumu:");
            GridData lblData = new GridData();
            lblData.horizontalSpan = 3;
            lbl.setLayoutData(lblData);

            txtStatus = new Text(dialogShell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
            GridData txtData = new GridData(GridData.FILL_BOTH);
            txtData.horizontalSpan = 3;
            txtStatus.setLayoutData(txtData);

            Button btnKes = new Button(dialogShell, SWT.PUSH);
            btnKes.setText("e-Arþiv Kes");
            btnKes.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
            btnKes.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    doKes();
                }
            });

            Button btnRefresh = new Button(dialogShell, SWT.PUSH);
            btnRefresh.setText("Durum Sorgula");
            btnRefresh.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    refreshStatus();
                }
            });

            Button btnClose = new Button(dialogShell, SWT.PUSH);
            btnClose.setText("Kapat");
            btnClose.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    dialogShell.dispose();
                }
            });

            refreshStatus();

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

    private void refreshStatus() {
        try {
            TurqEInvoiceStatus st = EinvoiceDAL.findStatus(transactionId);
            if (st == null) {
                txtStatus.setText("Bu fatura henüz e-Arþiv olarak kesilmedi.\n\n"
                        + "Göndermek için 'e-Arþiv Kes' düðmesine basýn.");
            } else {
                StringBuilder b = new StringBuilder();
                b.append("Belge tipi : ").append(nv(st.getDocumentType())).append("\n");
                b.append("Durum      : ").append(nv(st.getStatus())).append("\n");
                b.append("Entegratör : ").append(nv(st.getProvider())).append("\n");
                b.append("Seri       : ").append(nv(st.getInvoiceSeries())).append("\n");
                b.append("ETTN       : ").append(nv(st.getEttn())).append("\n");
                if (st.getPdfUrl() != null) {
                    b.append("PDF        : ").append(st.getPdfUrl()).append("\n");
                }
                if (st.getGibResponse() != null) {
                    b.append("\nYanýt:\n").append(st.getGibResponse());
                }
                txtStatus.setText(b.toString());
            }
        } catch (Exception e) {
            EngBLLogger.log(this.getClass(), e, getParent());
        }
    }

    private void doKes() {
        try {
            EInvoiceSettings settings = EInvoiceSettingsStore.load();
            if (settings.getActiveProfile(EInvoiceType.EARSIV) == null) {
                info("e-Arþiv için aktif entegratör profili tanýmlý deðil.\n"
                        + "Önce e-belge ayarlarýndan bir profil tanýmlayýn ve aktif edin.");
                return;
            }
            EInvoiceResult result = EinvoiceBLIssue.issue(
                    transactionId, EInvoiceType.EARSIV, settings, currentUser());
            if (result.isSuccess()) {
                info("e-Arþiv baþarýyla gönderildi.\nDurum: " + result.getStatus()
                        + (result.getEttn() != null ? "\nETTN: " + result.getEttn() : ""));
            } else {
                info("e-Arþiv gönderilemedi:\n" + result.getMessage());
            }
            refreshStatus();
        } catch (Exception e) {
            EngBLLogger.log(this.getClass(), e, getParent());
            info("Hata: " + e.getMessage());
        }
    }

    private void info(String msg) {
        MessageBox box = new MessageBox(dialogShell, SWT.ICON_INFORMATION | SWT.OK);
        box.setText("e-Arþiv");
        box.setMessage(msg);
        box.open();
    }

    private static String nv(String s) {
        return s == null ? "-" : s;
    }

    private static String currentUser() {
        try {
            String u = com.turquaz.engine.EngConfiguration.getString("username");
            if (u != null && u.length() > 0) {
                return u;
            }
        } catch (Throwable ignore) {
        }
        return "turquaz";
    }
}
