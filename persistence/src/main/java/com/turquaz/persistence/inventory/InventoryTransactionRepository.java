/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {

    /**
     * Bir kart × depo kombinasyonunun belirli bir tarihe (dahil) kadar olan
     * net stok miktarı: Σ(amount_in) − Σ(amount_out).
     */
    @Query("""
            SELECT COALESCE(SUM(t.amountIn), 0) - COALESCE(SUM(t.amountOut), 0)
            FROM InventoryTransaction t
            WHERE t.companyId = :companyId
              AND t.inventoryCardsId = :cardId
              AND t.inventoryWarehousesId = :warehouseId
              AND t.transactionsDate <= :asOf
            """)
    BigDecimal stockOnHand(
            @Param("companyId") UUID companyId,
            @Param("cardId") UUID cardId,
            @Param("warehouseId") UUID warehouseId,
            @Param("asOf") Instant asOf);

    /**
     * Kâr analizi için kart bazlı toplamlar (ortalama maliyet yöntemine girdi).
     * Belirli bir tarih aralığında girişler ve çıkışların miktar + tutar
     * toplamlarını döndürür.
     */
    @Query("""
            SELECT t.inventoryCardsId AS cardId,
                   COALESCE(SUM(t.amountIn), 0) AS totalAmountIn,
                   COALESCE(SUM(t.amountOut), 0) AS totalAmountOut,
                   COALESCE(SUM(CASE WHEN t.amountIn > 0 THEN t.totalPrice ELSE 0 END), 0) AS totalCostIn,
                   COALESCE(SUM(CASE WHEN t.amountOut > 0 THEN t.totalPrice ELSE 0 END), 0) AS totalRevenueOut
            FROM InventoryTransaction t
            WHERE t.companyId = :companyId
              AND t.transactionsDate BETWEEN :from AND :to
            GROUP BY t.inventoryCardsId
            """)
    List<CardTransactionTotalsRow> cardTotals(
            @Param("companyId") UUID companyId,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
