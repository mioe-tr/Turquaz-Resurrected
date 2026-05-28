/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_current_transactions")
public class CurrentTransaction extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "transactions_date", nullable = false)
    private Instant transactionsDate;

    @Column(name = "transactions_document_no", nullable = false)
    private String transactionsDocumentNo;

    @Column(name = "current_transaction_types_id", nullable = false)
    private UUID currentTransactionTypesId;

    @Column(name = "transactions_total_credit", nullable = false)
    private BigDecimal transactionsTotalCredit;

    @Column(name = "transactions_total_discount", nullable = false)
    private BigDecimal transactionsTotalDiscount;

    @Column(name = "transactions_total_dept", nullable = false)
    private BigDecimal transactionsTotalDept;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "transactions_definition", nullable = false, length = 250)
    private String transactionsDefinition;

    @Column(name = "total_credit_in_foreign_currency", nullable = false)
    private BigDecimal totalCreditInForeignCurrency;

    @Column(name = "total_dept_in_foreign_currency", nullable = false)
    private BigDecimal totalDeptInForeignCurrency;

    @Column(name = "total_discount_in_foreign_currency", nullable = false)
    private BigDecimal totalDiscountInForeignCurrency;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public Instant getTransactionsDate() { return transactionsDate; }
    public void setTransactionsDate(Instant v) { this.transactionsDate = v; }

    public String getTransactionsDocumentNo() { return transactionsDocumentNo; }
    public void setTransactionsDocumentNo(String v) { this.transactionsDocumentNo = v; }

    public UUID getCurrentTransactionTypesId() { return currentTransactionTypesId; }
    public void setCurrentTransactionTypesId(UUID v) { this.currentTransactionTypesId = v; }

    public BigDecimal getTransactionsTotalCredit() { return transactionsTotalCredit; }
    public void setTransactionsTotalCredit(BigDecimal v) { this.transactionsTotalCredit = v; }

    public BigDecimal getTransactionsTotalDiscount() { return transactionsTotalDiscount; }
    public void setTransactionsTotalDiscount(BigDecimal v) { this.transactionsTotalDiscount = v; }

    public BigDecimal getTransactionsTotalDept() { return transactionsTotalDept; }
    public void setTransactionsTotalDept(BigDecimal v) { this.transactionsTotalDept = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public String getTransactionsDefinition() { return transactionsDefinition; }
    public void setTransactionsDefinition(String v) { this.transactionsDefinition = v; }

    public BigDecimal getTotalCreditInForeignCurrency() { return totalCreditInForeignCurrency; }
    public void setTotalCreditInForeignCurrency(BigDecimal v) { this.totalCreditInForeignCurrency = v; }

    public BigDecimal getTotalDeptInForeignCurrency() { return totalDeptInForeignCurrency; }
    public void setTotalDeptInForeignCurrency(BigDecimal v) { this.totalDeptInForeignCurrency = v; }

    public BigDecimal getTotalDiscountInForeignCurrency() { return totalDiscountInForeignCurrency; }
    public void setTotalDiscountInForeignCurrency(BigDecimal v) { this.totalDiscountInForeignCurrency = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
