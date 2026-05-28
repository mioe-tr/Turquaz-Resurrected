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
import java.util.UUID;

@Entity
@Table(name = "turq_cheque_cheques_rolls")
public class ChequeChequesRoll extends CompanyScopedEntity {

    @Column(name = "cheque_rolls_id", nullable = false)
    private UUID chequeRollsId;

    @Column(name = "cheque_cheques_id", nullable = false)
    private UUID chequeChequesId;

    public UUID getChequeRollsId() { return chequeRollsId; }
    public void setChequeRollsId(UUID v) { this.chequeRollsId = v; }

    public UUID getChequeChequesId() { return chequeChequesId; }
    public void setChequeChequesId(UUID v) { this.chequeChequesId = v; }

}
