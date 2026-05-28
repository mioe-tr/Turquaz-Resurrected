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
import java.time.Instant;
import java.util.UUID;

/**
 * Tüm Turquaz entity'leri için ortak temel: UUIDv7 birincil anahtar ve denetim
 * alanları. {@code last_modified} alanı bazı eski tablolarda {@code update_date}
 * adıyla geçer — entity sınıfları gerektiğinde {@code @Column(name = ...)} ile
 * eşler.
 */
@MappedSuperclass
public abstract class AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "updated_by", nullable = false, length = 50)
    private String updatedBy;

    @Column(name = "last_modified", nullable = false)
    private Instant lastModified;

    @PrePersist
    void assignId() {
        if (id == null) {
            id = UuidV7.next();
        }
        Instant now = Instant.now();
        if (creationDate == null) {
            creationDate = now;
        }
        if (lastModified == null) {
            lastModified = now;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String v) { this.createdBy = v; }

    public Instant getCreationDate() { return creationDate; }
    public void setCreationDate(Instant v) { this.creationDate = v; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String v) { this.updatedBy = v; }

    public Instant getLastModified() { return lastModified; }
    public void setLastModified(Instant v) { this.lastModified = v; }
}
