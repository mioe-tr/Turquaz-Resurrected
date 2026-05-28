/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.accounting;

/** Muhasebe domain'i invariant ihlali. */
public class AccountingException extends RuntimeException {
    public AccountingException(String message) {
        super(message);
    }
}
