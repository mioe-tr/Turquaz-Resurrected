/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cheque;

import com.turquaz.persistence.BaseCompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "turq_cheque_transaction_type_groups")
public class ChequeTransactionTypeGroup extends BaseCompanyScopedEntity {

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "definition", length = 250)
    private String definition;

    public String getGroupName() { return groupName; }
    public void setGroupName(String v) { this.groupName = v; }

    public String getDefinition() { return definition; }
    public void setDefinition(String v) { this.definition = v; }

}
