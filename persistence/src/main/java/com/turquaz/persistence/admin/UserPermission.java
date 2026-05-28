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
@Table(name = "turq_user_permissions")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class UserPermission extends CompanyScopedEntity {

    @Column(name = "users_id", nullable = false)
    private UUID usersId;

    @Column(name = "modules_id", nullable = false)
    private UUID modulesId;

    @Column(name = "module_components_id", nullable = false)
    private UUID moduleComponentsId;

    @Column(name = "user_permissions_level", nullable = false)
    private UUID userPermissionsLevel;

    public UUID getUsersId() { return usersId; }
    public void setUsersId(UUID v) { this.usersId = v; }

    public UUID getModulesId() { return modulesId; }
    public void setModulesId(UUID v) { this.modulesId = v; }

    public UUID getModuleComponentsId() { return moduleComponentsId; }
    public void setModuleComponentsId(UUID v) { this.moduleComponentsId = v; }

    public UUID getUserPermissionsLevel() { return userPermissionsLevel; }
    public void setUserPermissionsLevel(UUID v) { this.userPermissionsLevel = v; }

}
