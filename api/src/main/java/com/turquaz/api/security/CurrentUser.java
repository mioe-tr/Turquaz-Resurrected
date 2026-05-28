/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.security;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * SecurityContext'teki JWT'den o anki kullanıcı + şirket id'sini çıkaran
 * yardımcı. Controller'lar doğrudan {@link #companyId()} ve
 * {@link #username()} çağırır; iş kuralları multi-tenant kapsama burada
 * uygulanır.
 */
public final class CurrentUser {

    private CurrentUser() {}

    public static Jwt jwt() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException("Kimlik doğrulama bağlamında JWT yok");
        }
        return jwt;
    }

    public static String username() {
        return jwt().getSubject();
    }

    public static UUID userId() {
        return UUID.fromString(jwt().getClaim("userId"));
    }

    public static UUID companyId() {
        return UUID.fromString(jwt().getClaim("companyId"));
    }
}
