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
@Table(name = "turq_order_in_groups")
public class OrderInGroup extends CompanyScopedEntity {

    @Column(name = "orders_id", nullable = false)
    private UUID ordersId;

    @Column(name = "order_groups_id", nullable = false)
    private UUID orderGroupsId;

    public UUID getOrdersId() { return ordersId; }
    public void setOrdersId(UUID v) { this.ordersId = v; }

    public UUID getOrderGroupsId() { return orderGroupsId; }
    public void setOrderGroupsId(UUID v) { this.orderGroupsId = v; }

}
