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
@Table(name = "turq_user_group")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class UserGroup extends CompanyScopedEntity {

    @Column(name = "groups_id", nullable = false)
    private UUID groupsId;

    @Column(name = "users_id", nullable = false)
    private UUID usersId;

    public UUID getGroupsId() { return groupsId; }
    public void setGroupsId(UUID v) { this.groupsId = v; }

    public UUID getUsersId() { return usersId; }
    public void setUsersId(UUID v) { this.usersId = v; }

}
