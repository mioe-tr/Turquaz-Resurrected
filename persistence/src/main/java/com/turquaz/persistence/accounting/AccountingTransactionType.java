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

@Entity
@Table(name = "turq_accounting_transaction_types")
public class AccountingTransactionType extends CompanyScopedEntity {

    @Column(name = "types_name", nullable = false)
    private String typesName;

    public String getTypesName() { return typesName; }
    public void setTypesName(String v) { this.typesName = v; }

}
