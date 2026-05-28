/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_current_accounting_accounts")
public class CurrentAccountingAccount extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "current_accounting_types_id", nullable = false)
    private UUID currentAccountingTypesId;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public UUID getCurrentAccountingTypesId() { return currentAccountingTypesId; }
    public void setCurrentAccountingTypesId(UUID v) { this.currentAccountingTypesId = v; }

}
