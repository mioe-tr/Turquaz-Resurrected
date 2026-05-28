/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.admin;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_user_permission_levels")
public class UserPermissionLevel extends BaseCompanyScopedEntity {

    @Column(name = "permission_level", nullable = false)
    private Integer permissionLevel;

    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    @Column(name = "permission_description", nullable = false, length = 50)
    private String permissionDescription;

    public Integer getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(Integer v) { this.permissionLevel = v; }

    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String v) { this.permissionName = v; }

    public String getPermissionDescription() { return permissionDescription; }
    public void setPermissionDescription(String v) { this.permissionDescription = v; }

}
