/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.core.BusinessRuleException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stok kartı (ürün) yönetim servisi.
 *
 * <p>Eski {@code com.turquaz.inventory.bl.InvBLCardAdd/Search/Update}
 * sınıflarının modern karşılığı. Aynı şirkette aynı {@code card_inventory_code}
 * tekildir.
 */
@Service
public class InventoryCardService {

    private final InventoryCardRepository repo;

    public InventoryCardService(InventoryCardRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public InventoryCard create(NewInventoryCard cmd) {
        if (repo.existsByCompanyIdAndCardInventoryCode(cmd.companyId(), cmd.code())) {
            throw new BusinessRuleException(
                    "Bu stok koduyla zaten kayıtlı bir kart var: " + cmd.code());
        }
        if (cmd.minimumAmount() != null && cmd.maximumAmount() != null
                && cmd.minimumAmount() > cmd.maximumAmount()) {
            throw new BusinessRuleException(
                    "Minimum miktar (" + cmd.minimumAmount() + ") maksimumdan ("
                            + cmd.maximumAmount() + ") büyük olamaz");
        }
        InventoryCard card = new InventoryCard();
        card.setCompanyId(cmd.companyId());
        card.setCardInventoryCode(cmd.code());
        card.setCardName(cmd.name());
        card.setCardDefinition(cmd.definition());
        card.setCardMinimumAmount(cmd.minimumAmount());
        card.setCardMaximumAmount(cmd.maximumAmount());
        card.setCardVat(cmd.vatRate());
        card.setCardDiscount(cmd.discountPercent());
        card.setCardSpecialVat(cmd.specialVatRate());
        card.setCardSpecialVatEach(java.math.BigDecimal.ZERO);
        card.setSpecVatForEach(false);
        card.setCreatedBy(cmd.currentUser());
        card.setUpdatedBy(cmd.currentUser());
        return repo.save(card);
    }

    @Transactional(readOnly = true)
    public InventoryCard findByCode(UUID companyId, String code) {
        return repo.findByCompanyIdAndCardInventoryCode(companyId, code)
                .orElseThrow(() -> new BusinessRuleException(
                        "Stok kartı bulunamadı: " + code));
    }

    @Transactional(readOnly = true)
    public List<InventoryCard> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }
}
