/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.AuthApi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/** Basit karşılama paneli — kullanıcı bilgisi + modül listesi yönlendirmesi. */
public class DashboardView {

    public DashboardView(Composite parent, AuthApi.LoginResponse session) {
        parent.setLayout(new GridLayout(1, false));
        Label title = new Label(parent, SWT.NONE);
        title.setText("Hoş geldiniz, " + session.username);
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Group g = new Group(parent, SWT.NONE);
        g.setText("Kullanıcı");
        g.setLayout(new GridLayout(2, false));
        g.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        addRow(g, "Kullanıcı adı", session.username);
        addRow(g, "Kullanıcı UUID", session.userId);
        addRow(g, "Şirket UUID", session.companyId);

        Label hint = new Label(parent, SWT.WRAP);
        hint.setText("Soldaki listeden bir modül seçin. Modüller ilk açılışta yüklenir; "
                + "yenilemek için her görünümün üstündeki \"Yenile\" düğmesini kullanın.");
        hint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    private void addRow(Composite parent, String label, String value) {
        Label l = new Label(parent, SWT.NONE);
        l.setText(label + ":");
        Label v = new Label(parent, SWT.NONE);
        v.setText(value == null ? "—" : value);
        v.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }
}
