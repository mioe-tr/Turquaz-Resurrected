/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.accounting;

import com.turquaz.core.money.Money;
import java.util.UUID;

/**
 * Yevmiye fişi satırı: bir hesabın borç veya alacak tarafına etki.
 *
 * <p>Değişmez (immutable) muhasebe satırı invariant'ları:
 * <ul>
 *   <li>{@code debit} ve {@code credit} negatif olamaz.</li>
 *   <li>Bir satırda {@code debit} veya {@code credit}'ten yalnızca biri sıfırdan
 *       büyük olabilir (XOR) — eski {@code TurqAccountingTransactionColumn}
 *       konvansiyonu: "tek tarafa kayıt".</li>
 *   <li>İkisi de sıfır olamaz — boş satır anlamsız.</li>
 * </ul>
 */
public record JournalLine(UUID accountId, Money debit, Money credit, String description) {

    public JournalLine {
        if (accountId == null) {
            throw new AccountingException("Hesap belirtilmemiş");
        }
        if (debit == null || credit == null) {
            throw new AccountingException("Borç/alacak null olamaz");
        }
        if (debit.isNegative() || credit.isNegative()) {
            throw new AccountingException("Borç/alacak negatif olamaz");
        }
        if (debit.isZero() && credit.isZero()) {
            throw new AccountingException("Borç ve alacak ikisi birden sıfır olamaz");
        }
        if (!debit.isZero() && !credit.isZero()) {
            throw new AccountingException(
                    "Bir satır aynı anda hem borç hem alacak içeremez (hesap: " + accountId + ")");
        }
    }

    /** Kısayol: borç satırı. */
    public static JournalLine debit(UUID accountId, Money amount, String description) {
        return new JournalLine(accountId, amount, Money.ZERO, description);
    }

    /** Kısayol: alacak satırı. */
    public static JournalLine credit(UUID accountId, Money amount, String description) {
        return new JournalLine(accountId, Money.ZERO, amount, description);
    }
}
