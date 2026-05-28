/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_current_transaction_bill")
public class CurrentTransactionBill extends CompanyScopedEntity {

    @Column(name = "current_transactions_id_close", nullable = false)
    private UUID currentTransactionsIdClose;

    @Column(name = "current_transactions_id_open", nullable = false)
    private UUID currentTransactionsIdOpen;

    public UUID getCurrentTransactionsIdClose() { return currentTransactionsIdClose; }
    public void setCurrentTransactionsIdClose(UUID v) { this.currentTransactionsIdClose = v; }

    public UUID getCurrentTransactionsIdOpen() { return currentTransactionsIdOpen; }
    public void setCurrentTransactionsIdOpen(UUID v) { this.currentTransactionsIdOpen = v; }

}
