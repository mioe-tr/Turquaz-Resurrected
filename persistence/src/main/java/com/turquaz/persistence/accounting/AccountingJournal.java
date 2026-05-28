/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "turq_accounting_journal")
public class AccountingJournal extends CompanyScopedEntity {

    @Column(name = "journal_date", nullable = false)
    private Instant journalDate;

    public Instant getJournalDate() { return journalDate; }
    public void setJournalDate(Instant v) { this.journalDate = v; }

}
