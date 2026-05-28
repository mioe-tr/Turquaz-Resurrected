/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mizan/bakiye sorguları için projeksiyon arayüzü. Spring Data JPA
 * interface-based projection.
 */
public interface AccountBalanceRow {
    UUID getAccountingAccountsId();

    BigDecimal getTotalDebit();

    BigDecimal getTotalCredit();
}
