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
@Table(name = "turq_cheque_transaction_types")
public class ChequeTransactionType extends CompanyScopedEntity {

    @Column(name = "transaction_typs_name", nullable = false, length = 50)
    private String transactionTypsName;

    @Column(name = "transaction_types_parent", nullable = false)
    private UUID transactionTypesParent;

    public String getTransactionTypsName() { return transactionTypsName; }
    public void setTransactionTypsName(String v) { this.transactionTypsName = v; }

    public UUID getTransactionTypesParent() { return transactionTypesParent; }
    public void setTransactionTypesParent(UUID v) { this.transactionTypesParent = v; }

}
