/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Çok kiracılı iş tablosu temeli: {@link AuditableEntity}'ye ek olarak
 * {@code company_id} taşır. Global (turq_companies, turq_modules,
 * turq_module_components, turq_engine_menu) tablolar bu sınıftan türetilmez.
 */
@MappedSuperclass
public abstract class CompanyScopedEntity extends AuditableEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
}
