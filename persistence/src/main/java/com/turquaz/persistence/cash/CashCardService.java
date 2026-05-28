/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cash;

import com.turquaz.core.BusinessRuleException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kasa kartı yönetim servisi. Eski {@code CashBLCashCardAdd/Search/Update}'in
 * modern karşılığı.
 *
 * <p>Her kasa kartı bir muhasebe hesabıyla ({@code accounting_accounts_id})
 * ilişkilendirilir; hareket postlanırken bu hesap yevmiye fişinde kullanılır.
 */
@Service
public class CashCardService {

    private final CashCardRepository repo;

    public CashCardService(CashCardRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CashCard create(
            UUID companyId, String name, String definition,
            UUID accountingAccountsId, String currentUser) {
        CashCard card = new CashCard();
        card.setCompanyId(companyId);
        card.setCashCardName(name);
        card.setCashCardDefinition(definition);
        card.setAccountingAccountsId(accountingAccountsId);
        card.setCreatedBy(currentUser);
        card.setUpdatedBy(currentUser);
        return repo.save(card);
    }

    @Transactional(readOnly = true)
    public CashCard get(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Kasa kartı bulunamadı: " + id));
    }

    @Transactional(readOnly = true)
    public List<CashCard> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }
}
