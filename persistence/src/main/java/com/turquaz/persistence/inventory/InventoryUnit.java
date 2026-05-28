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

@Entity
@Table(name = "turq_inventory_units")
public class InventoryUnit extends CompanyScopedEntity {

    @Column(name = "units_name", nullable = false, length = 50)
    private String unitsName;

    public String getUnitsName() { return unitsName; }
    public void setUnitsName(String v) { this.unitsName = v; }

}
