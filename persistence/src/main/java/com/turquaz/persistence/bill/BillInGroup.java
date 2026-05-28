/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_bill_in_groups")
public class BillInGroup extends CompanyScopedEntity {

    @Column(name = "bills_id", nullable = false)
    private UUID billsId;

    @Column(name = "bill_groups_id", nullable = false)
    private UUID billGroupsId;

    public UUID getBillsId() { return billsId; }
    public void setBillsId(UUID v) { this.billsId = v; }

    public UUID getBillGroupsId() { return billGroupsId; }
    public void setBillGroupsId(UUID v) { this.billGroupsId = v; }

}
