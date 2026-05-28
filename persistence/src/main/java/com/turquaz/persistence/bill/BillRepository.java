/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, UUID> {

    Optional<Bill> findByCompanyIdAndBillDocumentNo(UUID companyId, String docNo);

    boolean existsByCompanyIdAndBillDocumentNo(UUID companyId, String docNo);

    List<Bill> findByCompanyIdAndCurrentCardsId(UUID companyId, UUID currentCardId);
}
