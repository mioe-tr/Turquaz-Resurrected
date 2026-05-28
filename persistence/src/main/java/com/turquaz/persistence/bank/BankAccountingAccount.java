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
@Table(name = "turq_bank_accounting_accounts")
public class BankAccountingAccount extends CompanyScopedEntity {

    @Column(name = "banks_cards_id", nullable = false)
    private UUID banksCardsId;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "bank_accounting_types_id", nullable = false)
    private UUID bankAccountingTypesId;

    public UUID getBanksCardsId() { return banksCardsId; }
    public void setBanksCardsId(UUID v) { this.banksCardsId = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public UUID getBankAccountingTypesId() { return bankAccountingTypesId; }
    public void setBankAccountingTypesId(UUID v) { this.bankAccountingTypesId = v; }

}
