/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core;

/**
 * Domain seviyesi iş kuralı ihlali. (Örn: aynı kodda iki cari kart, stoktan
 * fazla çıkış, dengesiz fiş.) HTTP 4xx'e map'lenmesi gereken tüm istemci
 * hatalarının üst sınıfıdır.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
