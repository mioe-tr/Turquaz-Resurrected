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
import java.util.UUID;

@Entity
@Table(name = "turq_tradebill_tradebills_rolls")
public class TradebillTradebillsRoll extends CompanyScopedEntity {

    @Column(name = "tradebill_rolls_id", nullable = false)
    private UUID tradebillRollsId;

    @Column(name = "tradebill_tradebills_id", nullable = false)
    private UUID tradebillTradebillsId;

    public UUID getTradebillRollsId() { return tradebillRollsId; }
    public void setTradebillRollsId(UUID v) { this.tradebillRollsId = v; }

    public UUID getTradebillTradebillsId() { return tradebillTradebillsId; }
    public void setTradebillTradebillsId(UUID v) { this.tradebillTradebillsId = v; }

}
