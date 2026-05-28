/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cash;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "turq_cash_transaction_rows")
public class CashTransactionRow extends CompanyScopedEntity {

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "dept_amount", nullable = false)
    private BigDecimal deptAmount;

    @Column(name = "credit_amount", nullable = false)
    private BigDecimal creditAmount;

    @Column(name = "cash_transactions_id", nullable = false)
    private UUID cashTransactionsId;

    @Column(name = "transaction_definition", length = 250)
    private String transactionDefinition;

    @Column(name = "cash_cards_id", nullable = false)
    private UUID cashCardsId;

    @Column(name = "dept_amount_in_foreign_currency", nullable = false)
    private BigDecimal deptAmountInForeignCurrency;

    @Column(name = "credit_amount_in_foreign_currency", nullable = false)
    private BigDecimal creditAmountInForeignCurrency;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public BigDecimal getDeptAmount() { return deptAmount; }
    public void setDeptAmount(BigDecimal v) { this.deptAmount = v; }

    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal v) { this.creditAmount = v; }

    public UUID getCashTransactionsId() { return cashTransactionsId; }
    public void setCashTransactionsId(UUID v) { this.cashTransactionsId = v; }

    public String getTransactionDefinition() { return transactionDefinition; }
    public void setTransactionDefinition(String v) { this.transactionDefinition = v; }

    public UUID getCashCardsId() { return cashCardsId; }
    public void setCashCardsId(UUID v) { this.cashCardsId = v; }

    public BigDecimal getDeptAmountInForeignCurrency() { return deptAmountInForeignCurrency; }
    public void setDeptAmountInForeignCurrency(BigDecimal v) { this.deptAmountInForeignCurrency = v; }

    public BigDecimal getCreditAmountInForeignCurrency() { return creditAmountInForeignCurrency; }
    public void setCreditAmountInForeignCurrency(BigDecimal v) { this.creditAmountInForeignCurrency = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
