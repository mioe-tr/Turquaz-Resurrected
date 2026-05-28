/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_cheque_cheques")
public class ChequeCheque extends CompanyScopedEntity {

    @Column(name = "cheques_portfolio_no", nullable = false, length = 30)
    private String chequesPortfolioNo;

    @Column(name = "cheques_no", nullable = false, length = 50)
    private String chequesNo;

    @Column(name = "banks_id", nullable = false)
    private UUID banksId;

    @Column(name = "cheques_due_date", nullable = false)
    private Instant chequesDueDate;

    @Column(name = "cheques_debtor", nullable = false, length = 100)
    private String chequesDebtor;

    @Column(name = "cheques_payment_place", length = 50)
    private String chequesPaymentPlace;

    @Column(name = "cheques_value_date", nullable = false)
    private Instant chequesValueDate;

    @Column(name = "cheques_amount", nullable = false)
    private BigDecimal chequesAmount;

    @Column(name = "currencies_id", nullable = false)
    private UUID currenciesId;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "bank_branch_name", nullable = false, length = 100)
    private String bankBranchName;

    @Column(name = "cheques_type", nullable = false)
    private Integer chequesType;

    @Column(name = "bank_account_no", length = 100)
    private String bankAccountNo;

    @Column(name = "cheques_amount_in_foreign_currency", nullable = false)
    private BigDecimal chequesAmountInForeignCurrency;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public String getChequesPortfolioNo() { return chequesPortfolioNo; }
    public void setChequesPortfolioNo(String v) { this.chequesPortfolioNo = v; }

    public String getChequesNo() { return chequesNo; }
    public void setChequesNo(String v) { this.chequesNo = v; }

    public UUID getBanksId() { return banksId; }
    public void setBanksId(UUID v) { this.banksId = v; }

    public Instant getChequesDueDate() { return chequesDueDate; }
    public void setChequesDueDate(Instant v) { this.chequesDueDate = v; }

    public String getChequesDebtor() { return chequesDebtor; }
    public void setChequesDebtor(String v) { this.chequesDebtor = v; }

    public String getChequesPaymentPlace() { return chequesPaymentPlace; }
    public void setChequesPaymentPlace(String v) { this.chequesPaymentPlace = v; }

    public Instant getChequesValueDate() { return chequesValueDate; }
    public void setChequesValueDate(Instant v) { this.chequesValueDate = v; }

    public BigDecimal getChequesAmount() { return chequesAmount; }
    public void setChequesAmount(BigDecimal v) { this.chequesAmount = v; }

    public UUID getCurrenciesId() { return currenciesId; }
    public void setCurrenciesId(UUID v) { this.currenciesId = v; }

    public String getBankName() { return bankName; }
    public void setBankName(String v) { this.bankName = v; }

    public String getBankBranchName() { return bankBranchName; }
    public void setBankBranchName(String v) { this.bankBranchName = v; }

    public Integer getChequesType() { return chequesType; }
    public void setChequesType(Integer v) { this.chequesType = v; }

    public String getBankAccountNo() { return bankAccountNo; }
    public void setBankAccountNo(String v) { this.bankAccountNo = v; }

    public BigDecimal getChequesAmountInForeignCurrency() { return chequesAmountInForeignCurrency; }
    public void setChequesAmountInForeignCurrency(BigDecimal v) { this.chequesAmountInForeignCurrency = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
