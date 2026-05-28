/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.core.BusinessRuleException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Çek portföyü (rulo) yönetimi: çekleri rulolara yerleştir, ruloyu işle.
 * Eski {@code CheBLSearchChequeRoll}/{@code CheBLUpdateChequeRoll} karşılığı.
 *
 * <p>Çeki rulodan rulo'ya taşımak (devir/keside) için
 * {@link #assignChequeToRoll} kullanılır; bağlantı 2018'de eklenen
 * {@link ChequeChequesRoll} ara tablosunda tutulur.
 */
@Service
public class ChequeRollService {

    private final ChequeRollRepository rollRepo;
    private final ChequeChequesRollRepository linkRepo;
    private final ChequeChequeRepository chequeRepo;

    public ChequeRollService(
            ChequeRollRepository rollRepo,
            ChequeChequesRollRepository linkRepo,
            ChequeChequeRepository chequeRepo) {
        this.rollRepo = rollRepo;
        this.linkRepo = linkRepo;
        this.chequeRepo = chequeRepo;
    }

    @Transactional
    public ChequeRoll create(
            UUID companyId, String rollNo, LocalDate date,
            UUID transactionTypeId, UUID currentCardsId, UUID banksCardsId,
            UUID engineSequenceId, boolean sumChequeAmounts, String currentUser) {
        if (rollRepo.findByCompanyIdAndChequeRollNo(companyId, rollNo).isPresent()) {
            throw new BusinessRuleException("Bu çek portföy no zaten kayıtlı: " + rollNo);
        }
        ChequeRoll roll = new ChequeRoll();
        roll.setCompanyId(companyId);
        roll.setChequeRollNo(rollNo);
        roll.setChequeRollsDate(date.atStartOfDay(ZoneOffset.UTC).toInstant());
        roll.setChequeTransactionTypesId(transactionTypeId);
        roll.setCurrentCardsId(currentCardsId);
        roll.setBanksCardsId(banksCardsId);
        roll.setEngineSequencesId(engineSequenceId);
        roll.setSumChequeAmounts(sumChequeAmounts);
        roll.setCreatedBy(currentUser);
        roll.setUpdatedBy(currentUser);
        return rollRepo.save(roll);
    }

    @Transactional
    public ChequeChequesRoll assignChequeToRoll(
            UUID companyId, UUID chequeId, UUID rollId, String currentUser) {
        chequeRepo.findById(chequeId)
                .orElseThrow(() -> new BusinessRuleException("Çek bulunamadı: " + chequeId));
        rollRepo.findById(rollId)
                .orElseThrow(() -> new BusinessRuleException("Çek portföyü bulunamadı: " + rollId));

        ChequeChequesRoll link = new ChequeChequesRoll();
        link.setCompanyId(companyId);
        link.setChequeChequesId(chequeId);
        link.setChequeRollsId(rollId);
        link.setCreatedBy(currentUser);
        link.setUpdatedBy(currentUser);
        return linkRepo.save(link);
    }

    @Transactional(readOnly = true)
    public List<ChequeRoll> listAll(UUID companyId) {
        return rollRepo.findByCompanyId(companyId);
    }
}
