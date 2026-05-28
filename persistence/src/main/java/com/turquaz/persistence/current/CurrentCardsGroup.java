/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_current_cards_groups")
public class CurrentCardsGroup extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "current_groups_id", nullable = false)
    private UUID currentGroupsId;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public UUID getCurrentGroupsId() { return currentGroupsId; }
    public void setCurrentGroupsId(UUID v) { this.currentGroupsId = v; }

}
