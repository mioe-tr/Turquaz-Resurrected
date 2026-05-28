/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.core.money.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Yeni çek/senet komutu.
 *
 * @param type {@link ChequeService#TYPE_RECEIVED} veya {@link ChequeService#TYPE_GIVEN}
 */
public record NewCheque(
        UUID companyId,
        String chequeNo,
        String portfolioNo,
        UUID bankId,
        UUID currencyId,
        UUID exchangeRateId,
        BigDecimal exchangeRate,
        String bankName,
        String bankBranchName,
        String bankAccountNo,
        Money amount,
        String debtor,
        String paymentPlace,
        LocalDate dueDate,
        LocalDate valueDate,
        int type,
        String currentUser) {
}
