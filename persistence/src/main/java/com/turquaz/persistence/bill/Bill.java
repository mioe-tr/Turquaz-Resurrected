/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_bills")
public class Bill extends CompanyScopedEntity {

    @Column(name = "bills_type", nullable = false)
    private Integer billsType;

    @Column(name = "bills_date", nullable = false)
    private Instant billsDate;

    @Column(name = "bills_definition", nullable = false, length = 250)
    private String billsDefinition;

    @Column(name = "bills_printed", nullable = false)
    private Boolean billsPrinted;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "bill_document_no", nullable = false, length = 50)
    private String billDocumentNo;

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    public Integer getBillsType() { return billsType; }
    public void setBillsType(Integer v) { this.billsType = v; }

    public Instant getBillsDate() { return billsDate; }
    public void setBillsDate(Instant v) { this.billsDate = v; }

    public String getBillsDefinition() { return billsDefinition; }
    public void setBillsDefinition(String v) { this.billsDefinition = v; }

    public Boolean getBillsPrinted() { return billsPrinted; }
    public void setBillsPrinted(Boolean v) { this.billsPrinted = v; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean v) { this.isOpen = v; }

    public Instant getDueDate() { return dueDate; }
    public void setDueDate(Instant v) { this.dueDate = v; }

    public String getBillDocumentNo() { return billDocumentNo; }
    public void setBillDocumentNo(String v) { this.billDocumentNo = v; }

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

}
