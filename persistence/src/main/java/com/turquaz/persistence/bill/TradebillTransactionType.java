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
@Table(name = "turq_tradebill_transaction_types")
public class TradebillTransactionType extends CompanyScopedEntity {

    @Column(name = "transaction_types_name", nullable = false, length = 50)
    private String transactionTypesName;

    @Column(name = "transaction_types_parent", nullable = false)
    private Short transactionTypesParent;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    public String getTransactionTypesName() { return transactionTypesName; }
    public void setTransactionTypesName(String v) { this.transactionTypesName = v; }

    public Short getTransactionTypesParent() { return transactionTypesParent; }
    public void setTransactionTypesParent(Short v) { this.transactionTypesParent = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

}
