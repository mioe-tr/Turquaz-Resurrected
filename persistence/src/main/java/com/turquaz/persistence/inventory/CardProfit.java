/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.core.money.Money;
import com.turquaz.core.money.Quantity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Bir stok kartının belirli bir dönemdeki kâr analizi (ortalama maliyet
 * yöntemi).
 *
 * <p>{@code avgUnitCost = costIn / amountIn} (giriş yoksa sıfır).
 * {@code costOfSold = amountOut × avgUnitCost}.
 * {@code profit = revenueOut − costOfSold}.
 */
public record CardProfit(
        UUID cardId,
        Quantity amountIn,
        Quantity amountOut,
        Money costIn,
        Money revenueOut,
        Money avgUnitCost,
        Money costOfSold,
        Money profit) {

    public static CardProfit compute(
            UUID cardId,
            Quantity amountIn,
            Quantity amountOut,
            Money costIn,
            Money revenueOut) {
        Money avgUnitCost = amountIn.isZero()
                ? Money.ZERO
                : Money.of(costIn.amount().divide(amountIn.value(), 6, RoundingMode.HALF_EVEN));
        Money costOfSold = Money.of(amountOut.value().multiply(avgUnitCost.amount()));
        Money profit = revenueOut.subtract(costOfSold);
        return new CardProfit(cardId, amountIn, amountOut, costIn, revenueOut,
                avgUnitCost, costOfSold, profit);
    }
}
