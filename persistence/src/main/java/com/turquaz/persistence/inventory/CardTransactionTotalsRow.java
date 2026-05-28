/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import java.math.BigDecimal;
import java.util.UUID;

/** Kâr analizi için kart bazlı toplamlar (interface projection). */
public interface CardTransactionTotalsRow {
    UUID getCardId();

    BigDecimal getTotalAmountIn();

    BigDecimal getTotalAmountOut();

    BigDecimal getTotalCostIn();

    BigDecimal getTotalRevenueOut();
}
