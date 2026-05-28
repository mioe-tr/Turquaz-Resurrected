/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_bank_secondary_accounts")
public class BankSecondaryAccount extends CompanyScopedEntity {

    @Column(name = "account_name", nullable = false, length = 50)
    private String accountName;

    @Column(name = "account_code", nullable = false, length = 5)
    private String accountCode;

    public String getAccountName() { return accountName; }
    public void setAccountName(String v) { this.accountName = v; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String v) { this.accountCode = v; }

}
