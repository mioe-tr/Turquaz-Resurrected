/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountingTransactionColumnRepository extends JpaRepository<AccountingTransactionColumn, UUID> {

    /**
     * Mizan: belirli bir tarihe kadar her hesap için toplam borç ve alacak.
     * Tarih filtresi yevmiye fiş tarihi üzerinden uygulanır.
     */
    @Query("""
            SELECT c.accountingAccountsId AS accountingAccountsId,
                   COALESCE(SUM(c.deptAmount), 0) AS totalDebit,
                   COALESCE(SUM(c.creditAmount), 0) AS totalCredit
            FROM AccountingTransactionColumn c, AccountingTransaction t
            WHERE c.accountingTransactionsId = t.id
              AND c.companyId = :companyId
              AND t.transactionsDate <= :asOf
            GROUP BY c.accountingAccountsId
            """)
    List<AccountBalanceRow> trialBalance(
            @Param("companyId") UUID companyId,
            @Param("asOf") Instant asOf);

    /** Tek hesap için toplam borç. */
    @Query("""
            SELECT COALESCE(SUM(c.deptAmount), 0)
            FROM AccountingTransactionColumn c, AccountingTransaction t
            WHERE c.accountingTransactionsId = t.id
              AND c.companyId = :companyId
              AND c.accountingAccountsId = :accountId
              AND t.transactionsDate <= :asOf
            """)
    BigDecimal totalDebit(
            @Param("companyId") UUID companyId,
            @Param("accountId") UUID accountId,
            @Param("asOf") Instant asOf);

    /** Tek hesap için toplam alacak. */
    @Query("""
            SELECT COALESCE(SUM(c.creditAmount), 0)
            FROM AccountingTransactionColumn c, AccountingTransaction t
            WHERE c.accountingTransactionsId = t.id
              AND c.companyId = :companyId
              AND c.accountingAccountsId = :accountId
              AND t.transactionsDate <= :asOf
            """)
    BigDecimal totalCredit(
            @Param("companyId") UUID companyId,
            @Param("accountId") UUID accountId,
            @Param("asOf") Instant asOf);
}
