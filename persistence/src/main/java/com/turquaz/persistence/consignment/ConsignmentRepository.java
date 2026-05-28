/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.consignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsignmentRepository extends JpaRepository<Consignment, UUID> {

    Optional<Consignment> findByCompanyIdAndConsignmentDocumentNo(UUID companyId, String docNo);

    boolean existsByCompanyIdAndConsignmentDocumentNo(UUID companyId, String docNo);

    List<Consignment> findByCompanyIdAndCurrentCardsId(UUID companyId, UUID currentCardId);
}
