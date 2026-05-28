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

@Entity
@Table(name = "turq_current_transaction_types")
public class CurrentTransactionType extends CompanyScopedEntity {

    @Column(name = "transaction_type_name", nullable = false, length = 50)
    private String transactionTypeName;

    public String getTransactionTypeName() { return transactionTypeName; }
    public void setTransactionTypeName(String v) { this.transactionTypeName = v; }

}
