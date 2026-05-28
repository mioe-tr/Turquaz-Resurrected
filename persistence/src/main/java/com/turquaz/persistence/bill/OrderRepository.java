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

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByCompanyIdAndOrdersDocumentNo(UUID companyId, Integer docNo);

    List<Order> findByCompanyIdAndCurrentCardsId(UUID companyId, UUID currentCardId);

    List<Order> findByCompanyId(UUID companyId);
}
