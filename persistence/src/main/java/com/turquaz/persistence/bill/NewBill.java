/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import java.time.LocalDate;
import java.util.UUID;

/** Yeni fatura komutu. */
public record NewBill(
        UUID companyId,
        int type,
        LocalDate billDate,
        LocalDate dueDate,
        String documentNo,
        String definition,
        UUID currentCardId,
        UUID exchangeRateId,
        UUID engineSequenceId,
        String currentUser) {
}
