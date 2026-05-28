/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.core.money.Money;
import java.util.UUID;

/**
 * Mizan satırı: bir hesabın belirli bir kesite kadar toplam borç, toplam
 * alacak ve net bakiyesi.
 *
 * <p>{@code net} pozitifse borç bakiyesi, negatifse alacak bakiyesi. Hesabın
 * türüne göre yorumlanır (aktif hesap için borç bakiyesi normal,
 * pasif/gelir için alacak bakiyesi normal).
 */
public record TrialBalanceLine(UUID accountId, Money totalDebit, Money totalCredit) {

    public Money net() {
        return totalDebit.subtract(totalCredit);
    }
}
