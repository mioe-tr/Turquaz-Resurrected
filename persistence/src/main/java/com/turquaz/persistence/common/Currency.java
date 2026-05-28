/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.common;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_currencies")
public class Currency extends CompanyScopedEntity {

    @Column(name = "currencies_name", nullable = false, length = 30)
    private String currenciesName;

    @Column(name = "currencies_abbreviation", nullable = false, length = 5)
    private String currenciesAbbreviation;

    @Column(name = "currencies_country", nullable = false, length = 50)
    private String currenciesCountry;

    @Column(name = "default_currency", nullable = false)
    private Boolean defaultCurrency;

    @Column(name = "constant")
    private Boolean constant;

    public String getCurrenciesName() { return currenciesName; }
    public void setCurrenciesName(String v) { this.currenciesName = v; }

    public String getCurrenciesAbbreviation() { return currenciesAbbreviation; }
    public void setCurrenciesAbbreviation(String v) { this.currenciesAbbreviation = v; }

    public String getCurrenciesCountry() { return currenciesCountry; }
    public void setCurrenciesCountry(String v) { this.currenciesCountry = v; }

    public Boolean getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(Boolean v) { this.defaultCurrency = v; }

    public Boolean getConstant() { return constant; }
    public void setConstant(Boolean v) { this.constant = v; }

}
