/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Money;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cari kart (müşteri/tedarikçi) yönetim servisi.
 *
 * <p>Eski {@code com.turquaz.current.bl.CurBLCurrentCardAdd/Search/Update}
 * sınıflarının modern karşılığı. Anahtar invariant: aynı şirket içinde aynı
 * {@code cards_current_code} iki kez olamaz.
 */
@Service
public class CurrentCardService {

    private final CurrentCardRepository repo;

    public CurrentCardService(CurrentCardRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CurrentCard create(NewCurrentCard cmd) {
        if (repo.existsByCompanyIdAndCardsCurrentCode(cmd.companyId(), cmd.code())) {
            throw new BusinessRuleException(
                    "Bu cari koduyla zaten kayıtlı bir kart var: " + cmd.code());
        }
        CurrentCard card = new CurrentCard();
        card.setCompanyId(cmd.companyId());
        card.setCardsCurrentCode(cmd.code());
        card.setCardsName(cmd.name());
        card.setCardsDefinition(cmd.definition());
        card.setCardsAddress(cmd.address());
        card.setCardsTaxDepartment(cmd.taxDepartment());
        card.setCardsTaxNumber(cmd.taxNumber());
        card.setCardsCreditLimit(cmd.creditLimit().amount());
        card.setCardsRiskLimit(cmd.riskLimit().amount());
        card.setCardsDiscountRate(cmd.discountRate());
        card.setCardsDiscountPayment(cmd.discountPayment().amount());
        card.setDaysToValue(cmd.daysToValue());
        card.setCreatedBy(cmd.currentUser());
        card.setUpdatedBy(cmd.currentUser());
        return repo.save(card);
    }

    @Transactional(readOnly = true)
    public CurrentCard findByCode(UUID companyId, String code) {
        return repo.findByCompanyIdAndCardsCurrentCode(companyId, code)
                .orElseThrow(() -> new BusinessRuleException(
                        "Cari kart bulunamadı: " + code));
    }

    @Transactional(readOnly = true)
    public List<CurrentCard> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }

    @Transactional
    public CurrentCard updateLimits(UUID cardId, Money creditLimit, Money riskLimit, String currentUser) {
        CurrentCard card = repo.findById(cardId)
                .orElseThrow(() -> new BusinessRuleException("Cari kart bulunamadı: " + cardId));
        card.setCardsCreditLimit(creditLimit.amount());
        card.setCardsRiskLimit(riskLimit.amount());
        card.setUpdatedBy(currentUser);
        card.setLastModified(java.time.Instant.now());
        return repo.save(card);
    }
}
