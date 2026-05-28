/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.accounting;

import com.turquaz.core.money.Money;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Yevmiye fişi: bir tarihte, bir veya birden fazla hesabı etkileyen, birden
 * fazla satırdan oluşan değişmez (immutable) muhasebe işlemi.
 *
 * <p>Tek temel invariant: <b>Σborç = Σalacak</b>. Bu, çift taraflı muhasebenin
 * defter eşitliği koşuludur ve her fişte zorunludur. {@link #of(LocalDate,
 * String, String, List)} fabrikasında doğrulanır; servis bunu post ederken
 * kontrolü tekrar varsaymaz — burada yapıldığında DB'ye gitmeden hata alınır.
 */
public record JournalEntry(
        LocalDate transactionDate,
        String documentNo,
        String description,
        List<JournalLine> lines) {

    public JournalEntry {
        Objects.requireNonNull(transactionDate, "transactionDate null olamaz");
        Objects.requireNonNull(documentNo, "documentNo null olamaz");
        Objects.requireNonNull(description, "description null olamaz");
        Objects.requireNonNull(lines, "lines null olamaz");
        if (lines.isEmpty()) {
            throw new AccountingException("Yevmiye fişi en az bir satır içermeli");
        }
        // Tek satırlık fiş anlamsız (tek tarafa kayıt çift taraflıyı bozar)
        if (lines.size() < 2) {
            throw new AccountingException("Yevmiye fişi en az iki satır gerektirir (borç ve alacak)");
        }
        lines = List.copyOf(lines);

        Money totalDebit = totalDebit(lines);
        Money totalCredit = totalCredit(lines);
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new AccountingException(
                    "Borç ve alacak toplamları eşit değil (borç=" + totalDebit
                            + ", alacak=" + totalCredit + ")");
        }
    }

    public Money totalDebit() { return totalDebit(lines); }
    public Money totalCredit() { return totalCredit(lines); }

    private static Money totalDebit(List<JournalLine> ls) {
        return ls.stream().map(JournalLine::debit).reduce(Money.ZERO, Money::add);
    }

    private static Money totalCredit(List<JournalLine> ls) {
        return ls.stream().map(JournalLine::credit).reduce(Money.ZERO, Money::add);
    }
}
