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
@Table(name = "turq_accounting_account_classes")
public class AccountingAccountClass extends CompanyScopedEntity {

    @Column(name = "accounting_classes_name", nullable = false, length = 50)
    private String accountingClassesName;

    @Column(name = "accounting_classes_definition", nullable = false, length = 50)
    private String accountingClassesDefinition;

    public String getAccountingClassesName() { return accountingClassesName; }
    public void setAccountingClassesName(String v) { this.accountingClassesName = v; }

    public String getAccountingClassesDefinition() { return accountingClassesDefinition; }
    public void setAccountingClassesDefinition(String v) { this.accountingClassesDefinition = v; }

}
