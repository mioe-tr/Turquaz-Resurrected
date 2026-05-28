/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import java.util.UUID;

/** Yeni stok kartı için komut nesnesi. */
public record NewInventoryCard(
        UUID companyId,
        String code,
        String name,
        String definition,
        Integer minimumAmount,
        Integer maximumAmount,
        Integer vatRate,
        Integer discountPercent,
        Integer specialVatRate,
        String currentUser) {
}
