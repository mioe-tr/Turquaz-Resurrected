/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCardRepository extends JpaRepository<InventoryCard, UUID> {

    Optional<InventoryCard> findByCompanyIdAndCardInventoryCode(UUID companyId, String code);

    boolean existsByCompanyIdAndCardInventoryCode(UUID companyId, String code);

    List<InventoryCard> findByCompanyId(UUID companyId);
}
