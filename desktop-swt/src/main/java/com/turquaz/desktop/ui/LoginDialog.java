/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.ui;

import com.turquaz.desktop.rest.ApiException;
import com.turquaz.desktop.rest.AuthApi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Eski TurquazClient'in Login penceresinin modern SWT yeniden yazımı.
 * Başarılı login'de {@link AuthApi.LoginResponse} döndürür; iptalde null.
 */
public class LoginDialog {

    private final Display display;
    private final AuthApi authApi;
    private AuthApi.LoginResponse result;

    public LoginDialog(Display display, AuthApi authApi) {
        this.display = display;
        this.authApi = authApi;
    }

    public AuthApi.LoginResponse open() {
        Shell shell = new Shell(display, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
        shell.setText("Turquaz — Giriş");
        shell.setLayout(new GridLayout(2, false));
        shell.setSize(420, 200);

        new Label(shell, SWT.NONE).setText("Kullanıcı Adı:");
        Text username = new Text(shell, SWT.BORDER);
        username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(shell, SWT.NONE).setText("Parola:");
        Text password = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label error = new Label(shell, SWT.WRAP);
        error.setForeground(display.getSystemColor(SWT.COLOR_RED));
        GridData errorData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        errorData.heightHint = 30;
        error.setLayoutData(errorData);

        Button loginBtn = new Button(shell, SWT.PUSH);
        loginBtn.setText("Giriş");
        GridData btnData = new GridData(SWT.END, SWT.CENTER, true, false, 2, 1);
        loginBtn.setLayoutData(btnData);
        shell.setDefaultButton(loginBtn);

        loginBtn.addListener(SWT.Selection, e -> {
            try {
                result = authApi.login(username.getText().trim(), password.getText());
                shell.close();
            } catch (ApiException ex) {
                error.setText(ex.getMessage());
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        return result;
    }
}
