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
@Table(name = "turq_inventory_transaction_types")
public class InventoryTransactionType extends CompanyScopedEntity {

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    public String getTypeName() { return typeName; }
    public void setTypeName(String v) { this.typeName = v; }

}
