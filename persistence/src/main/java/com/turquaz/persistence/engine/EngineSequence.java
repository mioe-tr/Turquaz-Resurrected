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
import java.util.UUID;

@Entity
@Table(name = "turq_engine_sequences")
public class EngineSequence extends BaseCompanyScopedEntity {

    @Column(name = "modules_id", nullable = false)
    private UUID modulesId;

    public UUID getModulesId() { return modulesId; }
    public void setModulesId(UUID v) { this.modulesId = v; }

}
