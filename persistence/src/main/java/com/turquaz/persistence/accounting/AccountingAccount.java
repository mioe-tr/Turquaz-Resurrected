/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_accounting_accounts")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class AccountingAccount extends CompanyScopedEntity {

    @Column(name = "account_name", nullable = false, length = 250)
    private String accountName;

    @Column(name = "account_code", nullable = false, length = 50)
    private String accountCode;

    @Column(name = "parent_account", nullable = false)
    private UUID parentAccount;

    @Column(name = "top_account", nullable = false)
    private UUID topAccount;

    @Column(name = "accounting_types_id")
    private UUID accountingTypesId;

    @Column(name = "accounting_class_id")
    private UUID accountingClassId;

    public String getAccountName() { return accountName; }
    public void setAccountName(String v) { this.accountName = v; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String v) { this.accountCode = v; }

    public UUID getParentAccount() { return parentAccount; }
    public void setParentAccount(UUID v) { this.parentAccount = v; }

    public UUID getTopAccount() { return topAccount; }
    public void setTopAccount(UUID v) { this.topAccount = v; }

    public UUID getAccountingTypesId() { return accountingTypesId; }
    public void setAccountingTypesId(UUID v) { this.accountingTypesId = v; }

    public UUID getAccountingClassId() { return accountingClassId; }
    public void setAccountingClassId(UUID v) { this.accountingClassId = v; }

}
