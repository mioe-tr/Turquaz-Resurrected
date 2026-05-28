/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.money.Money;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JournalEntryTest {

    private static final UUID KASA = UUID.randomUUID();
    private static final UUID BANKA = UUID.randomUUID();
    private static final UUID GELIR = UUID.randomUUID();

    @Test
    void dengeli_fiş_oluşur() {
        JournalEntry entry = new JournalEntry(
                LocalDate.of(2026, 5, 28),
                "FIŞ-001",
                "Banka tahsilatı",
                List.of(
                        JournalLine.debit(BANKA, Money.of("1000"), "Banka hesabı"),
                        JournalLine.credit(GELIR, Money.of("1000"), "Satış geliri")));
        assertThat(entry.totalDebit()).isEqualTo(Money.of("1000"));
        assertThat(entry.totalCredit()).isEqualTo(Money.of("1000"));
        assertThat(entry.lines()).hasSize(2);
    }

    @Test
    void çoklu_satır_dengeli_fiş() {
        JournalEntry entry = new JournalEntry(
                LocalDate.now(),
                "FIŞ-002",
                "Karma fiş",
                List.of(
                        JournalLine.debit(KASA, Money.of("400"), "Nakit"),
                        JournalLine.debit(BANKA, Money.of("600"), "Banka"),
                        JournalLine.credit(GELIR, Money.of("1000"), "Toplam gelir")));
        assertThat(entry.totalDebit()).isEqualTo(Money.of("1000"));
        assertThat(entry.totalCredit()).isEqualTo(Money.of("1000"));
    }

    @Test
    void dengesiz_fiş_reddedilir() {
        assertThatThrownBy(() -> new JournalEntry(
                LocalDate.now(),
                "FIŞ-003",
                "Yanlış",
                List.of(
                        JournalLine.debit(BANKA, Money.of("100"), "x"),
                        JournalLine.credit(GELIR, Money.of("99.99"), "y"))))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("eşit değil");
    }

    @Test
    void tek_satır_reddedilir() {
        assertThatThrownBy(() -> new JournalEntry(
                LocalDate.now(),
                "FIŞ-004",
                "Tekli",
                List.of(JournalLine.debit(BANKA, Money.of("100"), "x"))))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("en az iki satır");
    }

    @Test
    void boş_satır_reddedilir() {
        assertThatThrownBy(() -> new JournalEntry(
                LocalDate.now(), "FIŞ-005", "Boş", List.of()))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("en az bir satır");
    }

    @Test
    void küsuratlı_dengeli_fiş() {
        // 100/3 = 33.3333... Yuvarlama farkı denge bozmamalı (kaynakta toplam 100.00)
        JournalEntry entry = new JournalEntry(
                LocalDate.now(),
                "FIŞ-006",
                "Küsurat",
                List.of(
                        JournalLine.debit(KASA, Money.of("33.3333"), "p1"),
                        JournalLine.debit(BANKA, Money.of("33.3333"), "p2"),
                        JournalLine.debit(GELIR, Money.of("33.3334"), "p3"),
                        JournalLine.credit(KASA, Money.of("100.0000"), "toplam")));
        assertThat(entry.totalDebit()).isEqualTo(Money.of("100"));
    }
}
