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

@Entity
@Table(name = "turq_bill_groups")
public class BillGroup extends CompanyScopedEntity {

    @Column(name = "groups_name", nullable = false, length = 50)
    private String groupsName;

    @Column(name = "group_description", nullable = false, length = 250)
    private String groupDescription;

    public String getGroupsName() { return groupsName; }
    public void setGroupsName(String v) { this.groupsName = v; }

    public String getGroupDescription() { return groupDescription; }
    public void setGroupDescription(String v) { this.groupDescription = v; }

}
