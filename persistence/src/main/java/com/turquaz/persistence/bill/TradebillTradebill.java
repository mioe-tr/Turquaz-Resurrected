/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_tradebill_tradebills")
public class TradebillTradebill extends CompanyScopedEntity {

    @Column(name = "tradebills_portfolio_no", nullable = false, length = 50)
    private String tradebillsPortfolioNo;

    @Column(name = "tradebill_due_date", nullable = false)
    private Instant tradebillDueDate;

    @Column(name = "tradebill_debtor", nullable = false, length = 100)
    private String tradebillDebtor;

    @Column(name = "tradebill_guarantor", nullable = false, length = 100)
    private String tradebillGuarantor;

    @Column(name = "tradebill_payment_place", nullable = false, length = 100)
    private String tradebillPaymentPlace;

    @Column(name = "tradebill_value_date", nullable = false)
    private Integer tradebillValueDate;

    @Column(name = "tradebill_amount", nullable = false)
    private BigDecimal tradebillAmount;

    @Column(name = "currencies_id", nullable = false)
    private UUID currenciesId;

    public String getTradebillsPortfolioNo() { return tradebillsPortfolioNo; }
    public void setTradebillsPortfolioNo(String v) { this.tradebillsPortfolioNo = v; }

    public Instant getTradebillDueDate() { return tradebillDueDate; }
    public void setTradebillDueDate(Instant v) { this.tradebillDueDate = v; }

    public String getTradebillDebtor() { return tradebillDebtor; }
    public void setTradebillDebtor(String v) { this.tradebillDebtor = v; }

    public String getTradebillGuarantor() { return tradebillGuarantor; }
    public void setTradebillGuarantor(String v) { this.tradebillGuarantor = v; }

    public String getTradebillPaymentPlace() { return tradebillPaymentPlace; }
    public void setTradebillPaymentPlace(String v) { this.tradebillPaymentPlace = v; }

    public Integer getTradebillValueDate() { return tradebillValueDate; }
    public void setTradebillValueDate(Integer v) { this.tradebillValueDate = v; }

    public BigDecimal getTradebillAmount() { return tradebillAmount; }
    public void setTradebillAmount(BigDecimal v) { this.tradebillAmount = v; }

    public UUID getCurrenciesId() { return currenciesId; }
    public void setCurrenciesId(UUID v) { this.currenciesId = v; }

}
