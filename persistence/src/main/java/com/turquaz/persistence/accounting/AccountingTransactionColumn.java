/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "turq_accounting_transaction_columns")
public class AccountingTransactionColumn extends CompanyScopedEntity {

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "dept_amount", nullable = false)
    private BigDecimal deptAmount;

    @Column(name = "credit_amount", nullable = false)
    private BigDecimal creditAmount;

    @Column(name = "accounting_transactions_id", nullable = false)
    private UUID accountingTransactionsId;

    @Column(name = "transaction_definition", nullable = false, length = 250)
    private String transactionDefinition;

    @Column(name = "rows_dept_in_base_currency", nullable = false)
    private BigDecimal rowsDeptInBaseCurrency;

    @Column(name = "rows_credit_in_base_currency", nullable = false)
    private BigDecimal rowsCreditInBaseCurrency;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public BigDecimal getDeptAmount() { return deptAmount; }
    public void setDeptAmount(BigDecimal v) { this.deptAmount = v; }

    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal v) { this.creditAmount = v; }

    public UUID getAccountingTransactionsId() { return accountingTransactionsId; }
    public void setAccountingTransactionsId(UUID v) { this.accountingTransactionsId = v; }

    public String getTransactionDefinition() { return transactionDefinition; }
    public void setTransactionDefinition(String v) { this.transactionDefinition = v; }

    public BigDecimal getRowsDeptInBaseCurrency() { return rowsDeptInBaseCurrency; }
    public void setRowsDeptInBaseCurrency(BigDecimal v) { this.rowsDeptInBaseCurrency = v; }

    public BigDecimal getRowsCreditInBaseCurrency() { return rowsCreditInBaseCurrency; }
    public void setRowsCreditInBaseCurrency(BigDecimal v) { this.rowsCreditInBaseCurrency = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
