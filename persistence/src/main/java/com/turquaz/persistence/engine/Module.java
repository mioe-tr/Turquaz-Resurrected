/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.engine;

import com.turquaz.persistence.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_modules")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class Module extends AuditableEntity {

    @Column(name = "modules_name", nullable = false, length = 100)
    private String modulesName;

    @Column(name = "module_description", nullable = false, length = 250)
    private String moduleDescription;

    public String getModulesName() { return modulesName; }
    public void setModulesName(String v) { this.modulesName = v; }

    public String getModuleDescription() { return moduleDescription; }
    public void setModuleDescription(String v) { this.moduleDescription = v; }

}
