/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.PostingContext;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Banka hareketi komutu.
 *
 * @param bankCardId banka hesabı kartı id'si
 * @param bankAccountingAccountId bankanın hesap planındaki id'si (102 vb.)
 * @param counterAccountId karşı taraf hesabı (kasa, cari, vb.)
 * @param bankTransactionsBillsId fiş başlık id'si (üst kayıt — banka fiş başlığı)
 * @param amount tutar (her zaman pozitif)
 * @param date hareket tarihi
 * @param documentNo belge no
 * @param description açıklama
 * @param context yevmiye postlama bağlamı (şirket, journal, sequence, kur, vb.)
 */
public record BankMovement(
        UUID bankCardId,
        UUID bankAccountingAccountId,
        UUID counterAccountId,
        UUID bankTransactionsBillsId,
        Money amount,
        LocalDate date,
        String documentNo,
        String description,
        PostingContext context) {
}
