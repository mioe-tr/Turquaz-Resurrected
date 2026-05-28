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
import java.util.UUID;

@Entity
@Table(name = "turq_banks_cards")
public class BanksCard extends CompanyScopedEntity {

    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Column(name = "bank_branch_name", nullable = false, length = 50)
    private String bankBranchName;

    @Column(name = "bank_account_no", nullable = false, length = 50)
    private String bankAccountNo;

    @Column(name = "currencies_id", nullable = false)
    private UUID currenciesId;

    @Column(name = "bank_definition", nullable = false, length = 250)
    private String bankDefinition;

    @Column(name = "bank_code", nullable = false, length = 100)
    private String bankCode;

    public String getBankName() { return bankName; }
    public void setBankName(String v) { this.bankName = v; }

    public String getBankBranchName() { return bankBranchName; }
    public void setBankBranchName(String v) { this.bankBranchName = v; }

    public String getBankAccountNo() { return bankAccountNo; }
    public void setBankAccountNo(String v) { this.bankAccountNo = v; }

    public UUID getCurrenciesId() { return currenciesId; }
    public void setCurrenciesId(UUID v) { this.currenciesId = v; }

    public String getBankDefinition() { return bankDefinition; }
    public void setBankDefinition(String v) { this.bankDefinition = v; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String v) { this.bankCode = v; }

}
