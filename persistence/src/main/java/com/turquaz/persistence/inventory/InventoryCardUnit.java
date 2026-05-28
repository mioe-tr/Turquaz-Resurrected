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
@Table(name = "turq_inventory_card_units")
public class InventoryCardUnit extends CompanyScopedEntity {

    @Column(name = "inventory_cards_id", nullable = false)
    private UUID inventoryCardsId;

    @Column(name = "card_units_factor", nullable = false)
    private BigDecimal cardUnitsFactor;

    @Column(name = "inventory_units_id", nullable = false)
    private UUID inventoryUnitsId;

    public UUID getInventoryCardsId() { return inventoryCardsId; }
    public void setInventoryCardsId(UUID v) { this.inventoryCardsId = v; }

    public BigDecimal getCardUnitsFactor() { return cardUnitsFactor; }
    public void setCardUnitsFactor(BigDecimal v) { this.cardUnitsFactor = v; }

    public UUID getInventoryUnitsId() { return inventoryUnitsId; }
    public void setInventoryUnitsId(UUID v) { this.inventoryUnitsId = v; }

}
