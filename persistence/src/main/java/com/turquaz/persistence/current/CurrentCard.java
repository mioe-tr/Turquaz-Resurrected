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

@Entity
@Table(name = "turq_current_cards")
public class CurrentCard extends CompanyScopedEntity {

    @Column(name = "cards_current_code", nullable = false, length = 25)
    private String cardsCurrentCode;

    @Column(name = "cards_name", nullable = false, length = 250)
    private String cardsName;

    @Column(name = "cards_definition", nullable = false, length = 250)
    private String cardsDefinition;

    @Column(name = "cards_address", nullable = false, length = 250)
    private String cardsAddress;

    @Column(name = "cards_discount_rate", nullable = false)
    private BigDecimal cardsDiscountRate;

    @Column(name = "cards_discount_payment", nullable = false)
    private BigDecimal cardsDiscountPayment;

    @Column(name = "cards_credit_limit", nullable = false)
    private BigDecimal cardsCreditLimit;

    @Column(name = "cards_risk_limit", nullable = false)
    private BigDecimal cardsRiskLimit;

    @Column(name = "cards_tax_department", nullable = false, length = 50)
    private String cardsTaxDepartment;

    @Column(name = "cards_tax_number", nullable = false, length = 50)
    private String cardsTaxNumber;

    @Column(name = "days_to_value")
    private Integer daysToValue;

    public String getCardsCurrentCode() { return cardsCurrentCode; }
    public void setCardsCurrentCode(String v) { this.cardsCurrentCode = v; }

    public String getCardsName() { return cardsName; }
    public void setCardsName(String v) { this.cardsName = v; }

    public String getCardsDefinition() { return cardsDefinition; }
    public void setCardsDefinition(String v) { this.cardsDefinition = v; }

    public String getCardsAddress() { return cardsAddress; }
    public void setCardsAddress(String v) { this.cardsAddress = v; }

    public BigDecimal getCardsDiscountRate() { return cardsDiscountRate; }
    public void setCardsDiscountRate(BigDecimal v) { this.cardsDiscountRate = v; }

    public BigDecimal getCardsDiscountPayment() { return cardsDiscountPayment; }
    public void setCardsDiscountPayment(BigDecimal v) { this.cardsDiscountPayment = v; }

    public BigDecimal getCardsCreditLimit() { return cardsCreditLimit; }
    public void setCardsCreditLimit(BigDecimal v) { this.cardsCreditLimit = v; }

    public BigDecimal getCardsRiskLimit() { return cardsRiskLimit; }
    public void setCardsRiskLimit(BigDecimal v) { this.cardsRiskLimit = v; }

    public String getCardsTaxDepartment() { return cardsTaxDepartment; }
    public void setCardsTaxDepartment(String v) { this.cardsTaxDepartment = v; }

    public String getCardsTaxNumber() { return cardsTaxNumber; }
    public void setCardsTaxNumber(String v) { this.cardsTaxNumber = v; }

    public Integer getDaysToValue() { return daysToValue; }
    public void setDaysToValue(Integer v) { this.daysToValue = v; }

}
