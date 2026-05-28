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
 * Denetim alanı taşımayan ama kiracıya özel olan tablolar için temel sınıf:
 * {@link BaseEntity}'ye {@code company_id} eklenmiş halidir.
 */
@MappedSuperclass
public abstract class BaseCompanyScopedEntity extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
}
