/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.consignment;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_consignments_in_group")
public class ConsignmentsInGroup extends CompanyScopedEntity {

    @Column(name = "consignment_id", nullable = false)
    private UUID consignmentId;

    @Column(name = "consignments_groups_id", nullable = false)
    private UUID consignmentsGroupsId;

    public UUID getConsignmentId() { return consignmentId; }
    public void setConsignmentId(UUID v) { this.consignmentId = v; }

    public UUID getConsignmentsGroupsId() { return consignmentsGroupsId; }
    public void setConsignmentsGroupsId(UUID v) { this.consignmentsGroupsId = v; }

}
