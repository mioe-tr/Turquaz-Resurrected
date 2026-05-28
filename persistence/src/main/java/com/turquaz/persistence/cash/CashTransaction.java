/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cash;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_cash_transactions")
public class CashTransaction extends CompanyScopedEntity {

    @Column(name = "cash_transactions_types_id", nullable = false)
    private UUID cashTransactionsTypesId;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Column(name = "transaction_definition", length = 250)
    private String transactionDefinition;

    @Column(name = "document_no", length = 100)
    private String documentNo;

    public UUID getCashTransactionsTypesId() { return cashTransactionsTypesId; }
    public void setCashTransactionsTypesId(UUID v) { this.cashTransactionsTypesId = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public Instant getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Instant v) { this.transactionDate = v; }

    public String getTransactionDefinition() { return transactionDefinition; }
    public void setTransactionDefinition(String v) { this.transactionDefinition = v; }

    public String getDocumentNo() { return documentNo; }
    public void setDocumentNo(String v) { this.documentNo = v; }

}
