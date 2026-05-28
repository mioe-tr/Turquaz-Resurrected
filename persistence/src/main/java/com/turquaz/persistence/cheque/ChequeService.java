/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Money;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Çek/Senet kart yönetim servisi. Eski {@code CheBLSaveChequeTransaction},
 * {@code CheBLSearchCheques}, {@code CheBLUpdateCheque} sınıflarının modern
 * karşılığı.
 *
 * <p>Çek türü: {@link #TYPE_RECEIVED alınan (1)} veya {@link #TYPE_GIVEN
 * verilen (2)}. Aynı banka × {@code cheques_no} kombinasyonu tekildir.
 */
@Service
public class ChequeService {

    public static final int TYPE_RECEIVED = 1;
    public static final int TYPE_GIVEN = 2;

    private final ChequeChequeRepository repo;

    public ChequeService(ChequeChequeRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ChequeCheque create(NewCheque cmd) {
        if (cmd.type() != TYPE_RECEIVED && cmd.type() != TYPE_GIVEN) {
            throw new BusinessRuleException(
                    "Geçersiz çek türü: " + cmd.type() + " (1=alınan, 2=verilen)");
        }
        if (cmd.amount().isZero() || cmd.amount().isNegative()) {
            throw new BusinessRuleException("Çek tutarı pozitif olmalı");
        }
        if (cmd.dueDate().isBefore(cmd.valueDate())) {
            throw new BusinessRuleException(
                    "Vade tarihi keşide (value) tarihinden önce olamaz");
        }
        if (repo.findByCompanyIdAndChequesNoAndBanksId(
                cmd.companyId(), cmd.chequeNo(), cmd.bankId()).isPresent()) {
            throw new BusinessRuleException(
                    "Bu banka için bu çek numarası zaten kayıtlı: " + cmd.chequeNo());
        }
        ChequeCheque cheque = new ChequeCheque();
        cheque.setCompanyId(cmd.companyId());
        cheque.setChequesNo(cmd.chequeNo());
        cheque.setChequesPortfolioNo(cmd.portfolioNo());
        cheque.setBanksId(cmd.bankId());
        cheque.setCurrenciesId(cmd.currencyId());
        cheque.setBankName(cmd.bankName());
        cheque.setBankBranchName(cmd.bankBranchName());
        cheque.setBankAccountNo(cmd.bankAccountNo());
        cheque.setChequesAmount(cmd.amount().amount());
        cheque.setChequesAmountInForeignCurrency(
                cmd.amount().toBaseCurrency(cmd.exchangeRate()).amount());
        cheque.setChequesDebtor(cmd.debtor());
        cheque.setChequesPaymentPlace(cmd.paymentPlace());
        cheque.setChequesDueDate(cmd.dueDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        cheque.setChequesValueDate(cmd.valueDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        cheque.setChequesType(cmd.type());
        cheque.setExchangeRateId(cmd.exchangeRateId());
        cheque.setCreatedBy(cmd.currentUser());
        cheque.setUpdatedBy(cmd.currentUser());
        return repo.save(cheque);
    }

    @Transactional(readOnly = true)
    public List<ChequeCheque> listReceived(UUID companyId) {
        return repo.findByCompanyIdAndChequesType(companyId, TYPE_RECEIVED);
    }

    @Transactional(readOnly = true)
    public List<ChequeCheque> listGiven(UUID companyId) {
        return repo.findByCompanyIdAndChequesType(companyId, TYPE_GIVEN);
    }
}
