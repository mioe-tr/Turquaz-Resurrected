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

public interface ChequeRollRepository extends JpaRepository<ChequeRoll, UUID> {

    Optional<ChequeRoll> findByCompanyIdAndChequeRollNo(UUID companyId, String no);

    List<ChequeRoll> findByCompanyId(UUID companyId);
}
