/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanksTransactionBillRepository extends JpaRepository<BanksTransactionBill, UUID> {
}
