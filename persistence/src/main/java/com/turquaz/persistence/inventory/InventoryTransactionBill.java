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
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_transaction_bills")
public class InventoryTransactionBill extends CompanyScopedEntity {

    @Column(name = "bill_date", nullable = false)
    private Instant billDate;

    @Column(name = "bill_definition", nullable = false, length = 250)
    private String billDefinition;

    @Column(name = "bill_type", nullable = false)
    private Integer billType;

    @Column(name = "engine_sequence", nullable = false)
    private UUID engineSequence;

    @Column(name = "bill_document_no", nullable = false, length = 25)
    private String billDocumentNo;

    @Column(name = "warehouse_in", nullable = false)
    private UUID warehouseIn;

    @Column(name = "warehouse_out", nullable = false)
    private UUID warehouseOut;

    public Instant getBillDate() { return billDate; }
    public void setBillDate(Instant v) { this.billDate = v; }

    public String getBillDefinition() { return billDefinition; }
    public void setBillDefinition(String v) { this.billDefinition = v; }

    public Integer getBillType() { return billType; }
    public void setBillType(Integer v) { this.billType = v; }

    public UUID getEngineSequence() { return engineSequence; }
    public void setEngineSequence(UUID v) { this.engineSequence = v; }

    public String getBillDocumentNo() { return billDocumentNo; }
    public void setBillDocumentNo(String v) { this.billDocumentNo = v; }

    public UUID getWarehouseIn() { return warehouseIn; }
    public void setWarehouseIn(UUID v) { this.warehouseIn = v; }

    public UUID getWarehouseOut() { return warehouseOut; }
    public void setWarehouseOut(UUID v) { this.warehouseOut = v; }

}
