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
@Table(name = "turq_inventory_groups")
public class InventoryGroup extends CompanyScopedEntity {

    @Column(name = "groups_name", nullable = false, length = 50)
    private String groupsName;

    @Column(name = "groups_description", nullable = false, length = 250)
    private String groupsDescription;

    @Column(name = "parent_group", nullable = false)
    private UUID parentGroup;

    public String getGroupsName() { return groupsName; }
    public void setGroupsName(String v) { this.groupsName = v; }

    public String getGroupsDescription() { return groupsDescription; }
    public void setGroupsDescription(String v) { this.groupsDescription = v; }

    public UUID getParentGroup() { return parentGroup; }
    public void setParentGroup(UUID v) { this.parentGroup = v; }

}
