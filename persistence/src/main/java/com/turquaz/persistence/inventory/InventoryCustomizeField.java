/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_customize_fields")
public class InventoryCustomizeField extends BaseCompanyScopedEntity {

    @Column(name = "customize_type_id", nullable = false)
    private UUID customizeTypeId;

    @Column(name = "field_value", nullable = false, length = 50)
    private String fieldValue;

    @Column(name = "inventory_card_id", nullable = false)
    private UUID inventoryCardId;

    public UUID getCustomizeTypeId() { return customizeTypeId; }
    public void setCustomizeTypeId(UUID v) { this.customizeTypeId = v; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String v) { this.fieldValue = v; }

    public UUID getInventoryCardId() { return inventoryCardId; }
    public void setInventoryCardId(UUID v) { this.inventoryCardId = v; }

}
