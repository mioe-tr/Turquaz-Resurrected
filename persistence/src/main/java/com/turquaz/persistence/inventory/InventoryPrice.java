/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_prices")
public class InventoryPrice extends CompanyScopedEntity {

    @Column(name = "inventory_cards_id", nullable = false)
    private UUID inventoryCardsId;

    @Column(name = "prices_type", nullable = false)
    private Boolean pricesType;

    @Column(name = "currencies_id", nullable = false)
    private UUID currenciesId;

    @Column(name = "prices_amount", nullable = false)
    private BigDecimal pricesAmount;

    public UUID getInventoryCardsId() { return inventoryCardsId; }
    public void setInventoryCardsId(UUID v) { this.inventoryCardsId = v; }

    public Boolean getPricesType() { return pricesType; }
    public void setPricesType(Boolean v) { this.pricesType = v; }

    public UUID getCurrenciesId() { return currenciesId; }
    public void setCurrenciesId(UUID v) { this.currenciesId = v; }

    public BigDecimal getPricesAmount() { return pricesAmount; }
    public void setPricesAmount(BigDecimal v) { this.pricesAmount = v; }

}
