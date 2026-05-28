/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeChequeRepository extends JpaRepository<ChequeCheque, UUID> {

    Optional<ChequeCheque> findByCompanyIdAndChequesNoAndBanksId(UUID companyId, String no, UUID banksId);

    List<ChequeCheque> findByCompanyId(UUID companyId);

    List<ChequeCheque> findByCompanyIdAndChequesType(UUID companyId, Integer type);
}
