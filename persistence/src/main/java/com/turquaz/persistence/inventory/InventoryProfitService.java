/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.core.money.Money;
import com.turquaz.core.money.Quantity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stok kâr analizi servisi (ortalama maliyet yöntemi). Eski
 * {@code com.turquaz.inventory.bl.InvBLProfitAnalysis}'in modern karşılığı.
 *
 * <p>Her stok kartı için belirli bir dönemde:
 * <ol>
 *   <li>Giriş toplam miktar/tutarı ve çıkış toplam miktar/tutarı toplanır.</li>
 *   <li>Ortalama birim maliyet = toplam alış / toplam giriş miktarı.</li>
 *   <li>Satılan malın maliyeti = çıkış miktarı × ortalama birim maliyet.</li>
 *   <li>Kâr = satış geliri − satılan malın maliyeti.</li>
 * </ol>
 */
@Service
public class InventoryProfitService {

    private final InventoryTransactionRepository txRepo;

    public InventoryProfitService(InventoryTransactionRepository txRepo) {
        this.txRepo = txRepo;
    }

    @Transactional(readOnly = true)
    public List<CardProfit> report(UUID companyId, Instant from, Instant to) {
        return txRepo.cardTotals(companyId, from, to).stream()
                .map(r -> CardProfit.compute(
                        r.getCardId(),
                        new Quantity(r.getTotalAmountIn()),
                        new Quantity(r.getTotalAmountOut()),
                        Money.of(r.getTotalCostIn()),
                        Money.of(r.getTotalRevenueOut())))
                .toList();
    }
}
