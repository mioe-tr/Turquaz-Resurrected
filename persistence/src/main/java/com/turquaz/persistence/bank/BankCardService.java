/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.core.BusinessRuleException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Banka hesap kartı yönetim servisi. Eski {@code BankBLBankCardAdd/Search/Update}
 * sınıflarının modern karşılığı. Aynı şirkette {@code bank_code} tekildir.
 */
@Service
public class BankCardService {

    private final BanksCardRepository repo;

    public BankCardService(BanksCardRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public BanksCard create(NewBankCard cmd) {
        if (repo.existsByCompanyIdAndBankCode(cmd.companyId(), cmd.code())) {
            throw new BusinessRuleException(
                    "Bu banka koduyla zaten kayıtlı bir hesap var: " + cmd.code());
        }
        BanksCard card = new BanksCard();
        card.setCompanyId(cmd.companyId());
        card.setBankCode(cmd.code());
        card.setBankName(cmd.bankName());
        card.setBankBranchName(cmd.branchName());
        card.setBankAccountNo(cmd.accountNo());
        card.setBankDefinition(cmd.definition());
        card.setCurrenciesId(cmd.currencyId());
        card.setCreatedBy(cmd.currentUser());
        card.setUpdatedBy(cmd.currentUser());
        return repo.save(card);
    }

    @Transactional(readOnly = true)
    public BanksCard findByCode(UUID companyId, String code) {
        return repo.findByCompanyIdAndBankCode(companyId, code)
                .orElseThrow(() -> new BusinessRuleException("Banka kartı bulunamadı: " + code));
    }

    @Transactional(readOnly = true)
    public List<BanksCard> listAll(UUID companyId) {
        return repo.findByCompanyId(companyId);
    }
}
