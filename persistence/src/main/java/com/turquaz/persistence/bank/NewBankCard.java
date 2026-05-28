/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import java.util.UUID;

/** Yeni banka kartı için komut nesnesi. */
public record NewBankCard(
        UUID companyId,
        String code,
        String bankName,
        String branchName,
        String accountNo,
        String definition,
        UUID currencyId,
        String currentUser) {
}
