/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.common;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_currency_exchange_rates")
public class CurrencyExchangeRate extends BaseCompanyScopedEntity {

    @Column(name = "exhange_rates_date", nullable = false)
    private Instant exhangeRatesDate;

    @Column(name = "base_currency_id", nullable = false)
    private UUID baseCurrencyId;

    @Column(name = "exchange_currency_id", nullable = false)
    private UUID exchangeCurrencyId;

    @Column(name = "exchange_ratio", nullable = false)
    private BigDecimal exchangeRatio;

    public Instant getExhangeRatesDate() { return exhangeRatesDate; }
    public void setExhangeRatesDate(Instant v) { this.exhangeRatesDate = v; }

    public UUID getBaseCurrencyId() { return baseCurrencyId; }
    public void setBaseCurrencyId(UUID v) { this.baseCurrencyId = v; }

    public UUID getExchangeCurrencyId() { return exchangeCurrencyId; }
    public void setExchangeCurrencyId(UUID v) { this.exchangeCurrencyId = v; }

    public BigDecimal getExchangeRatio() { return exchangeRatio; }
    public void setExchangeRatio(BigDecimal v) { this.exchangeRatio = v; }

}
