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
@Table(name = "turq_accounting_account_types")
public class AccountingAccountType extends CompanyScopedEntity {

    @Column(name = "accounting_types_name", nullable = false, length = 50)
    private String accountingTypesName;

    @Column(name = "accounting_types_definition", nullable = false, length = 250)
    private String accountingTypesDefinition;

    public String getAccountingTypesName() { return accountingTypesName; }
    public void setAccountingTypesName(String v) { this.accountingTypesName = v; }

    public String getAccountingTypesDefinition() { return accountingTypesDefinition; }
    public void setAccountingTypesDefinition(String v) { this.accountingTypesDefinition = v; }

}
