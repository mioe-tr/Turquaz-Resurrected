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

@Entity
@Table(name = "turq_users")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class User extends CompanyScopedEntity {

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "users_password", nullable = false, length = 250)
    private String usersPassword;

    @Column(name = "users_real_name", nullable = false, length = 250)
    private String usersRealName;

    @Column(name = "users_description", nullable = false, length = 250)
    private String usersDescription;

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }

    public String getUsersPassword() { return usersPassword; }
    public void setUsersPassword(String v) { this.usersPassword = v; }

    public String getUsersRealName() { return usersRealName; }
    public void setUsersRealName(String v) { this.usersRealName = v; }

    public String getUsersDescription() { return usersDescription; }
    public void setUsersDescription(String v) { this.usersDescription = v; }

}
