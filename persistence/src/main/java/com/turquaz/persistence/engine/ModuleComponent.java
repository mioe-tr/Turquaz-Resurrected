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
import java.util.UUID;

@Entity
@Table(name = "turq_module_components")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class ModuleComponent extends AuditableEntity {

    @Column(name = "modules_id", nullable = false)
    private UUID modulesId;

    @Column(name = "components_name", nullable = false, length = 100)
    private String componentsName;

    @Column(name = "components_description", nullable = false, length = 250)
    private String componentsDescription;

    public UUID getModulesId() { return modulesId; }
    public void setModulesId(UUID v) { this.modulesId = v; }

    public String getComponentsName() { return componentsName; }
    public void setComponentsName(String v) { this.componentsName = v; }

    public String getComponentsDescription() { return componentsDescription; }
    public void setComponentsDescription(String v) { this.componentsDescription = v; }

}
