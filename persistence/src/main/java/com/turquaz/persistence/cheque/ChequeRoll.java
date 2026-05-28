/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_cheque_rolls")
public class ChequeRoll extends CompanyScopedEntity {

    @Column(name = "cheque_transaction_types_id", nullable = false)
    private UUID chequeTransactionTypesId;

    @Column(name = "cheque_rolls_date", nullable = false)
    private Instant chequeRollsDate;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "cheque_roll_no", nullable = false, length = 50)
    private String chequeRollNo;

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "banks_cards_id", nullable = false)
    private UUID banksCardsId;

    @Column(name = "sum_cheque_amounts", nullable = false)
    private Boolean sumChequeAmounts;

    public UUID getChequeTransactionTypesId() { return chequeTransactionTypesId; }
    public void setChequeTransactionTypesId(UUID v) { this.chequeTransactionTypesId = v; }

    public Instant getChequeRollsDate() { return chequeRollsDate; }
    public void setChequeRollsDate(Instant v) { this.chequeRollsDate = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public String getChequeRollNo() { return chequeRollNo; }
    public void setChequeRollNo(String v) { this.chequeRollNo = v; }

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public UUID getBanksCardsId() { return banksCardsId; }
    public void setBanksCardsId(UUID v) { this.banksCardsId = v; }

    public Boolean getSumChequeAmounts() { return sumChequeAmounts; }
    public void setSumChequeAmounts(Boolean v) { this.sumChequeAmounts = v; }

}
