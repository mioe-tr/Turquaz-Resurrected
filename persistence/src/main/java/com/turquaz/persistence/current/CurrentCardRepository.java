/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentCardRepository extends JpaRepository<CurrentCard, UUID> {

    Optional<CurrentCard> findByCompanyIdAndCardsCurrentCode(UUID companyId, String code);

    boolean existsByCompanyIdAndCardsCurrentCode(UUID companyId, String code);

    List<CurrentCard> findByCompanyId(UUID companyId);
}
