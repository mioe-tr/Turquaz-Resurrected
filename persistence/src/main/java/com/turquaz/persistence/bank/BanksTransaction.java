/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "turq_banks_transactions")
public class BanksTransaction extends CompanyScopedEntity {

    @Column(name = "bank_transactions_bills_id", nullable = false)
    private UUID bankTransactionsBillsId;

    @Column(name = "dept_amount", nullable = false)
    private BigDecimal deptAmount;

    @Column(name = "credit_amount", nullable = false)
    private BigDecimal creditAmount;

    @Column(name = "banks_cards_id", nullable = false)
    private UUID banksCardsId;

    @Column(name = "dept_amount_in_foreign_currency", nullable = false)
    private BigDecimal deptAmountInForeignCurrency;

    @Column(name = "credit_amount_in_foreign_currency", nullable = false)
    private BigDecimal creditAmountInForeignCurrency;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public UUID getBankTransactionsBillsId() { return bankTransactionsBillsId; }
    public void setBankTransactionsBillsId(UUID v) { this.bankTransactionsBillsId = v; }

    public BigDecimal getDeptAmount() { return deptAmount; }
    public void setDeptAmount(BigDecimal v) { this.deptAmount = v; }

    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal v) { this.creditAmount = v; }

    public UUID getBanksCardsId() { return banksCardsId; }
    public void setBanksCardsId(UUID v) { this.banksCardsId = v; }

    public BigDecimal getDeptAmountInForeignCurrency() { return deptAmountInForeignCurrency; }
    public void setDeptAmountInForeignCurrency(BigDecimal v) { this.deptAmountInForeignCurrency = v; }

    public BigDecimal getCreditAmountInForeignCurrency() { return creditAmountInForeignCurrency; }
    public void setCreditAmountInForeignCurrency(BigDecimal v) { this.creditAmountInForeignCurrency = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
