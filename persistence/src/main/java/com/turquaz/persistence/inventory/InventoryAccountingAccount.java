/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_accounting_accounts")
public class InventoryAccountingAccount extends CompanyScopedEntity {

    @Column(name = "inventory_cards_id", nullable = false)
    private UUID inventoryCardsId;

    @Column(name = "accounting_accounts_id", nullable = false)
    private UUID accountingAccountsId;

    @Column(name = "inventory_accounting_types_id", nullable = false)
    private UUID inventoryAccountingTypesId;

    public UUID getInventoryCardsId() { return inventoryCardsId; }
    public void setInventoryCardsId(UUID v) { this.inventoryCardsId = v; }

    public UUID getAccountingAccountsId() { return accountingAccountsId; }
    public void setAccountingAccountsId(UUID v) { this.accountingAccountsId = v; }

    public UUID getInventoryAccountingTypesId() { return inventoryAccountingTypesId; }
    public void setInventoryAccountingTypesId(UUID v) { this.inventoryAccountingTypesId = v; }

}
