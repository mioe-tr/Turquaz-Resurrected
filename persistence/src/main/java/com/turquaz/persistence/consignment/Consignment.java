/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.consignment;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_consignments")
public class Consignment extends CompanyScopedEntity {

    @Column(name = "consignments_date", nullable = false)
    private Instant consignmentsDate;

    @Column(name = "consignments_definition", nullable = false, length = 250)
    private String consignmentsDefinition;

    @Column(name = "consignments_type", nullable = false)
    private Integer consignmentsType;

    @Column(name = "consignments_printed", nullable = false)
    private Boolean consignmentsPrinted;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "consignment_document_no", nullable = false, length = 50)
    private String consignmentDocumentNo;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    @Column(name = "bill_document_no", nullable = false, length = 50)
    private String billDocumentNo;

    public Instant getConsignmentsDate() { return consignmentsDate; }
    public void setConsignmentsDate(Instant v) { this.consignmentsDate = v; }

    public String getConsignmentsDefinition() { return consignmentsDefinition; }
    public void setConsignmentsDefinition(String v) { this.consignmentsDefinition = v; }

    public Integer getConsignmentsType() { return consignmentsType; }
    public void setConsignmentsType(Integer v) { this.consignmentsType = v; }

    public Boolean getConsignmentsPrinted() { return consignmentsPrinted; }
    public void setConsignmentsPrinted(Boolean v) { this.consignmentsPrinted = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public String getConsignmentDocumentNo() { return consignmentDocumentNo; }
    public void setConsignmentDocumentNo(String v) { this.consignmentDocumentNo = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

    public String getBillDocumentNo() { return billDocumentNo; }
    public void setBillDocumentNo(String v) { this.billDocumentNo = v; }

}
