/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cash;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_cash_transaction_types")
public class CashTransactionType extends CompanyScopedEntity {

    @Column(name = "cash_transation_type_name", nullable = false, length = 100)
    private String cashTransationTypeName;

    public String getCashTransationTypeName() { return cashTransationTypeName; }
    public void setCashTransationTypeName(String v) { this.cashTransationTypeName = v; }

}
