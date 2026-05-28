/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@link JournalService#post} çağrısı için gerekli FK ve denetim bağlamı.
 *
 * <p>Eski {@code AccBLTransactionAdd.saveAccTransactionFromUI} method'unun
 * argümanlarına denk düşer; modernde değişmez bir kayıt olarak verilir,
 * "{@code HashMap argMap}" gibi kaybolup gitmez.
 */
public record PostingContext(
        UUID companyId,
        UUID journalId,
        UUID transactionTypeId,
        UUID moduleId,
        UUID engineSequenceId,
        UUID exchangeRateId,
        BigDecimal exchangeRate,
        String currentUser) {
}
