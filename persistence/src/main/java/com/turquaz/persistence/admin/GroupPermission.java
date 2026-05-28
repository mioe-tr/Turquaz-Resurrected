/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.admin;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_group_permissions")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class GroupPermission extends CompanyScopedEntity {

    @Column(name = "groups_id", nullable = false)
    private UUID groupsId;

    @Column(name = "modules_id", nullable = false)
    private UUID modulesId;

    @Column(name = "module_components_id", nullable = false)
    private UUID moduleComponentsId;

    @Column(name = "group_permissions_level", nullable = false)
    private Integer groupPermissionsLevel;

    public UUID getGroupsId() { return groupsId; }
    public void setGroupsId(UUID v) { this.groupsId = v; }

    public UUID getModulesId() { return modulesId; }
    public void setModulesId(UUID v) { this.modulesId = v; }

    public UUID getModuleComponentsId() { return moduleComponentsId; }
    public void setModuleComponentsId(UUID v) { this.moduleComponentsId = v; }

    public Integer getGroupPermissionsLevel() { return groupPermissionsLevel; }
    public void setGroupPermissionsLevel(Integer v) { this.groupPermissionsLevel = v; }

}
