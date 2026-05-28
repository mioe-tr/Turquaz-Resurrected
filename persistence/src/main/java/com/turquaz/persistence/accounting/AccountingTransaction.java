/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_accounting_transactions")
public class AccountingTransaction extends CompanyScopedEntity {

    @Column(name = "accounting_journal_id", nullable = false)
    private UUID accountingJournalId;

    @Column(name = "accounting_transaction_types_id", nullable = false)
    private UUID accountingTransactionTypesId;

    @Column(name = "transactions_date", nullable = false)
    private Instant transactionsDate;

    @Column(name = "module_id", nullable = false)
    private UUID moduleId;

    @Column(name = "transaction_document_no", nullable = false, length = 50)
    private String transactionDocumentNo;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "transaction_description", nullable = false, length = 250)
    private String transactionDescription;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    public UUID getAccountingJournalId() { return accountingJournalId; }
    public void setAccountingJournalId(UUID v) { this.accountingJournalId = v; }

    public UUID getAccountingTransactionTypesId() { return accountingTransactionTypesId; }
    public void setAccountingTransactionTypesId(UUID v) { this.accountingTransactionTypesId = v; }

    public Instant getTransactionsDate() { return transactionsDate; }
    public void setTransactionsDate(Instant v) { this.transactionsDate = v; }

    public UUID getModuleId() { return moduleId; }
    public void setModuleId(UUID v) { this.moduleId = v; }

    public String getTransactionDocumentNo() { return transactionDocumentNo; }
    public void setTransactionDocumentNo(String v) { this.transactionDocumentNo = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public String getTransactionDescription() { return transactionDescription; }
    public void setTransactionDescription(String v) { this.transactionDescription = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

}
