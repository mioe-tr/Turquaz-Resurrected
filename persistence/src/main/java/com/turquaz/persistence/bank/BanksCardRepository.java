/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanksCardRepository extends JpaRepository<BanksCard, UUID> {

    Optional<BanksCard> findByCompanyIdAndBankCode(UUID companyId, String code);

    boolean existsByCompanyIdAndBankCode(UUID companyId, String code);

    List<BanksCard> findByCompanyId(UUID companyId);
}
