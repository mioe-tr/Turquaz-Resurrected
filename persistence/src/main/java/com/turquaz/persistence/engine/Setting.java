/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.engine;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_settings")
public class Setting extends BaseCompanyScopedEntity {

    @Column(name = "database_version", nullable = false, length = 50)
    private String databaseVersion;

    public String getDatabaseVersion() { return databaseVersion; }
    public void setDatabaseVersion(String v) { this.databaseVersion = v; }

}
