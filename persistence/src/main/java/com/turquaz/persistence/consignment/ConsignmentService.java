/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.consignment;

import com.turquaz.core.BusinessRuleException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Konsinye yönetim servisi. Eski {@code ConBLAddConsignment}/
 * {@code ConBLUpdateConsignment}/{@code ConBLSearchConsignment} karşılığı.
 *
 * <p>Konsinye türü: {@link #TYPE_OUT verilen (1)} veya {@link #TYPE_IN
 * alınan (2)}.
 */
@Service
public class ConsignmentService {

    public static final int TYPE_OUT = 1;
    public static final int TYPE_IN = 2;

    private final ConsignmentRepository repo;

    public ConsignmentService(ConsignmentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Consignment create(NewConsignment cmd) {
        if (cmd.type() != TYPE_OUT && cmd.type() != TYPE_IN) {
            throw new BusinessRuleException(
                    "Geçersiz konsinye türü: " + cmd.type() + " (1=verilen, 2=alınan)");
        }
        if (repo.existsByCompanyIdAndConsignmentDocumentNo(cmd.companyId(), cmd.documentNo())) {
            throw new BusinessRuleException(
                    "Bu konsinye belge numarası zaten kayıtlı: " + cmd.documentNo());
        }
        Consignment c = new Consignment();
        c.setCompanyId(cmd.companyId());
        c.setConsignmentsDate(cmd.date().atStartOfDay(ZoneOffset.UTC).toInstant());
        c.setConsignmentsDefinition(cmd.definition());
        c.setConsignmentsType(cmd.type());
        c.setConsignmentsPrinted(false);
        c.setEngineSequencesId(cmd.engineSequenceId());
        c.setCurrentCardsId(cmd.currentCardId());
        c.setConsignmentDocumentNo(cmd.documentNo());
        c.setExchangeRateId(cmd.exchangeRateId());
        c.setBillDocumentNo(cmd.referenceBillNo());
        c.setCreatedBy(cmd.currentUser());
        c.setUpdatedBy(cmd.currentUser());
        return repo.save(c);
    }

    @Transactional
    public Consignment markPrinted(UUID consignmentId, String currentUser) {
        Consignment c = repo.findById(consignmentId)
                .orElseThrow(() -> new BusinessRuleException("Konsinye bulunamadı: " + consignmentId));
        c.setConsignmentsPrinted(true);
        c.setUpdatedBy(currentUser);
        c.setLastModified(java.time.Instant.now());
        return repo.save(c);
    }

    @Transactional(readOnly = true)
    public List<Consignment> listByCurrentCard(UUID companyId, UUID currentCardId) {
        return repo.findByCompanyIdAndCurrentCardsId(companyId, currentCardId);
    }
}
