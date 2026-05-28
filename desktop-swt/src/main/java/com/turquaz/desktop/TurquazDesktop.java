/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop;

import com.turquaz.desktop.rest.AuthApi;
import com.turquaz.desktop.rest.RestClient;
import com.turquaz.desktop.ui.LoginDialog;
import com.turquaz.desktop.ui.MainShell;
import org.eclipse.swt.widgets.Display;

/**
 * Turquaz SWT masaüstü istemcisi.
 *
 * <p>Sunucu URL'si {@code TURQUAZ_API_URL} çevre değişkeninden okunur,
 * varsayılan {@code http://localhost:8080}.
 */
public class TurquazDesktop {

    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("TURQUAZ_API_URL", "http://localhost:8080");
        RestClient rest = new RestClient(baseUrl);

        Display display = new Display();
        try {
            LoginDialog login = new LoginDialog(display, new AuthApi(rest));
            AuthApi.LoginResponse session = login.open();
            if (session == null) {
                return;
            }
            new MainShell(display, rest, session).open();
        } finally {
            display.dispose();
        }
    }
}
