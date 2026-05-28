/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence;

import com.turquaz.core.util.UuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.util.UUID;

/**
 * Denetim alanı taşımayan tablolar için minimum temel sınıf: yalnızca UUIDv7
 * birincil anahtar.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UuidV7.next();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
