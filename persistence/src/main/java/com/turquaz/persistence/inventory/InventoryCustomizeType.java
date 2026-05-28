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

@Entity
@Table(name = "turq_inventory_customize_types")
public class InventoryCustomizeType extends BaseCompanyScopedEntity {

    @Column(name = "field_name", nullable = false, length = 50)
    private String fieldName;

    public String getFieldName() { return fieldName; }
    public void setFieldName(String v) { this.fieldName = v; }

}
