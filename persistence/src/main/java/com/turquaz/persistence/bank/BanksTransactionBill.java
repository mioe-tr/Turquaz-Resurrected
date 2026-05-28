/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_banks_transaction_bills")
public class BanksTransactionBill extends CompanyScopedEntity {

    @Column(name = "transaction_bill_date", nullable = false)
    private Instant transactionBillDate;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "transaction_bill_definition", nullable = false, length = 250)
    private String transactionBillDefinition;

    @Column(name = "transaction_bill_no", nullable = false, length = 100)
    private String transactionBillNo;

    @Column(name = "banks_transaction_types_id", nullable = false)
    private UUID banksTransactionTypesId;

    public Instant getTransactionBillDate() { return transactionBillDate; }
    public void setTransactionBillDate(Instant v) { this.transactionBillDate = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public String getTransactionBillDefinition() { return transactionBillDefinition; }
    public void setTransactionBillDefinition(String v) { this.transactionBillDefinition = v; }

    public String getTransactionBillNo() { return transactionBillNo; }
    public void setTransactionBillNo(String v) { this.transactionBillNo = v; }

    public UUID getBanksTransactionTypesId() { return banksTransactionTypesId; }
    public void setBanksTransactionTypesId(UUID v) { this.banksTransactionTypesId = v; }

}
