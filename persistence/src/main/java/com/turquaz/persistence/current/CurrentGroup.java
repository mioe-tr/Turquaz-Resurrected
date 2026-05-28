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

@Entity
@Table(name = "turq_current_groups")
public class CurrentGroup extends CompanyScopedEntity {

    @Column(name = "groups_name", nullable = false, length = 50)
    private String groupsName;

    @Column(name = "groups_description", nullable = false, length = 250)
    private String groupsDescription;

    public String getGroupsName() { return groupsName; }
    public void setGroupsName(String v) { this.groupsName = v; }

    public String getGroupsDescription() { return groupsDescription; }
    public void setGroupsDescription(String v) { this.groupsDescription = v; }

}
