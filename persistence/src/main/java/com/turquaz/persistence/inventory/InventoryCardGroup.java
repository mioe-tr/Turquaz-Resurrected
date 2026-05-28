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
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_card_groups")
public class InventoryCardGroup extends CompanyScopedEntity {

    @Column(name = "inventory_cards_id", nullable = false)
    private UUID inventoryCardsId;

    @Column(name = "inventory_groups_id", nullable = false)
    private UUID inventoryGroupsId;

    public UUID getInventoryCardsId() { return inventoryCardsId; }
    public void setInventoryCardsId(UUID v) { this.inventoryCardsId = v; }

    public UUID getInventoryGroupsId() { return inventoryGroupsId; }
    public void setInventoryGroupsId(UUID v) { this.inventoryGroupsId = v; }

}
