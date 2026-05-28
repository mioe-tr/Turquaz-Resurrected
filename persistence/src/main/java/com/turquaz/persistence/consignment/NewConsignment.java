/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.consignment;

import java.time.LocalDate;
import java.util.UUID;

/** Yeni konsinye komutu. */
public record NewConsignment(
        UUID companyId,
        int type,
        LocalDate date,
        String documentNo,
        String referenceBillNo,
        String definition,
        UUID currentCardId,
        UUID exchangeRateId,
        UUID engineSequenceId,
        String currentUser) {
}
