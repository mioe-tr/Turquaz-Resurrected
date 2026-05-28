/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_current_accounting_types")
public class CurrentAccountingType extends BaseCompanyScopedEntity {

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "definition", nullable = false, length = 100)
    private String definition;

    public String getTypeName() { return typeName; }
    public void setTypeName(String v) { this.typeName = v; }

    public String getDefinition() { return definition; }
    public void setDefinition(String v) { this.definition = v; }

}
