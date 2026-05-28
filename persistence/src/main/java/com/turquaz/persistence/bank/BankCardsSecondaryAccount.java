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
@Table(name = "turq_bank_cards_secondary_accounts")
public class BankCardsSecondaryAccount extends CompanyScopedEntity {

    @Column(name = "bank_cards_id", nullable = false)
    private UUID bankCardsId;

    @Column(name = "bank_secondary_accounts_id", nullable = false)
    private UUID bankSecondaryAccountsId;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "bank_definition", nullable = false, length = 250)
    private String bankDefinition;

    public UUID getBankCardsId() { return bankCardsId; }
    public void setBankCardsId(UUID v) { this.bankCardsId = v; }

    public UUID getBankSecondaryAccountsId() { return bankSecondaryAccountsId; }
    public void setBankSecondaryAccountsId(UUID v) { this.bankSecondaryAccountsId = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public String getBankDefinition() { return bankDefinition; }
    public void setBankDefinition(String v) { this.bankDefinition = v; }

}
