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
import java.util.UUID;

@Entity
@Table(name = "turq_cash_cards")
public class CashCard extends CompanyScopedEntity {

    @Column(name = "cash_card_name", nullable = false, length = 250)
    private String cashCardName;

    @Column(name = "cash_card_definition", nullable = false, length = 250)
    private String cashCardDefinition;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    public String getCashCardName() { return cashCardName; }
    public void setCashCardName(String v) { this.cashCardName = v; }

    public String getCashCardDefinition() { return cashCardDefinition; }
    public void setCashCardDefinition(String v) { this.cashCardDefinition = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

}
