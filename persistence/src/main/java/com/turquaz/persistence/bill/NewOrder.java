/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.core.money.Money;
import java.time.LocalDate;
import java.util.UUID;

/** Yeni sipariş komutu. */
public record NewOrder(
        UUID companyId,
        int type,
        Integer documentNo,
        LocalDate orderDate,
        LocalDate dueDate,
        LocalDate deliverDate,
        UUID currentCardId,
        UUID billId,
        String definition,
        Integer discountRatePercent,
        Integer vatPercent,
        Money discountAmount,
        Money charges,
        Money vatAmount,
        Money totalAmount,
        String currentUser) {
}
