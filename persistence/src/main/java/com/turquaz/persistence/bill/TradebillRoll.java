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
@Table(name = "turq_tradebill_rolls")
public class TradebillRoll extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "banks_cards_id", nullable = false)
    private UUID banksCardsId;

    @Column(name = "tradebill_transaction_types_id", nullable = false)
    private UUID tradebillTransactionTypesId;

    @Column(name = "tradebill_rolls_date", nullable = false)
    private Instant tradebillRollsDate;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public UUID getBanksCardsId() { return banksCardsId; }
    public void setBanksCardsId(UUID v) { this.banksCardsId = v; }

    public UUID getTradebillTransactionTypesId() { return tradebillTransactionTypesId; }
    public void setTradebillTransactionTypesId(UUID v) { this.tradebillTransactionTypesId = v; }

    public Instant getTradebillRollsDate() { return tradebillRollsDate; }
    public void setTradebillRollsDate(Instant v) { this.tradebillRollsDate = v; }

}
