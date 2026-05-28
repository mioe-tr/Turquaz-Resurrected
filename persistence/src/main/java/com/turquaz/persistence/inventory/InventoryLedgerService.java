/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Quantity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stok defteri sorguları ve bütünlük kontrolleri.
 *
 * <p>Eski {@code com.turquaz.inventory.bl.InvBLInventoryLedger}'nin modern
 * karşılığı. Mevcut stok = Σ(amount_in) − Σ(amount_out) belirli bir tarihe
 * kadar; negatif stoğa izin verilmez (çıkışta {@link #ensureSufficient}).
 */
@Service
public class InventoryLedgerService {

    private final InventoryTransactionRepository txRepo;

    public InventoryLedgerService(InventoryTransactionRepository txRepo) {
        this.txRepo = txRepo;
    }

    @Transactional(readOnly = true)
    public Quantity stockOnHand(UUID companyId, UUID cardId, UUID warehouseId, Instant asOf) {
        BigDecimal net = txRepo.stockOnHand(companyId, cardId, warehouseId, asOf);
        return new Quantity(net == null ? BigDecimal.ZERO : net);
    }

    @Transactional(readOnly = true)
    public Quantity currentStock(UUID companyId, UUID cardId, UUID warehouseId) {
        return stockOnHand(companyId, cardId, warehouseId, Instant.now());
    }

    /**
     * Çıkış yapılmadan önce yeterli stok var mı kontrolü. Yetersizse
     * {@link BusinessRuleException} fırlatır.
     */
    @Transactional(readOnly = true)
    public void ensureSufficient(
            UUID companyId, UUID cardId, UUID warehouseId,
            Quantity requested, Instant asOf) {
        Quantity available = stockOnHand(companyId, cardId, warehouseId, asOf);
        if (available.compareTo(requested) < 0) {
            throw new BusinessRuleException(
                    "Yetersiz stok (mevcut=" + available + ", talep=" + requested + ")");
        }
    }
}
