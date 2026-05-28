/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.core.money.Money;
import java.math.BigDecimal;
import java.util.UUID;

/** Yeni cari kart için komut nesnesi. */
public record NewCurrentCard(
        UUID companyId,
        String code,
        String name,
        String definition,
        String address,
        String taxDepartment,
        String taxNumber,
        Money creditLimit,
        Money riskLimit,
        BigDecimal discountRate,
        Money discountPayment,
        Integer daysToValue,
        String currentUser) {
}
