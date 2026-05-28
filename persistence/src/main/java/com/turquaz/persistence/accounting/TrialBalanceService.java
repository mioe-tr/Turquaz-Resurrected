/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.core.money.Money;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mizan + hesap bakiyesi servisi. Eski {@code EngBLAccountingAccounts} ve
 * {@code AccBLAccountSearch}'in modern karşılığı.
 *
 * <p>Çift taraplı muhasebe defter eşitliği {@link #ensureBooksBalance}
 * tarafından doğrulanır: tüm hesapların toplam borcu ile toplam alacağı eşit
 * olmalı (her fiş zaten dengeli kaydedildiği için bu her zaman geçerlidir;
 * uyumsuzluk veri bozulmasının kanıtıdır).
 */
@Service
public class TrialBalanceService {

    private final AccountingTransactionColumnRepository colRepo;

    public TrialBalanceService(AccountingTransactionColumnRepository colRepo) {
        this.colRepo = colRepo;
    }

    @Transactional(readOnly = true)
    public List<TrialBalanceLine> trialBalance(UUID companyId, Instant asOf) {
        return colRepo.trialBalance(companyId, asOf).stream()
                .map(r -> new TrialBalanceLine(
                        r.getAccountingAccountsId(),
                        Money.of(r.getTotalDebit()),
                        Money.of(r.getTotalCredit())))
                .toList();
    }

    @Transactional(readOnly = true)
    public TrialBalanceLine accountBalance(UUID companyId, UUID accountId, Instant asOf) {
        Money debit = Money.of(colRepo.totalDebit(companyId, accountId, asOf));
        Money credit = Money.of(colRepo.totalCredit(companyId, accountId, asOf));
        return new TrialBalanceLine(accountId, debit, credit);
    }

    /**
     * Defter eşitliği bütünlük kontrolü: Σ(borç) = Σ(alacak). Her yevmiye fişi
     * dengeli postlandığı için bu her zaman geçerli olmalı; ihlal veri
     * bozulmasını gösterir.
     */
    @Transactional(readOnly = true)
    public void ensureBooksBalance(UUID companyId, Instant asOf) {
        List<TrialBalanceLine> lines = trialBalance(companyId, asOf);
        Money debit = lines.stream().map(TrialBalanceLine::totalDebit).reduce(Money.ZERO, Money::add);
        Money credit = lines.stream().map(TrialBalanceLine::totalCredit).reduce(Money.ZERO, Money::add);
        if (debit.compareTo(credit) != 0) {
            throw new com.turquaz.core.accounting.AccountingException(
                    "Defter eşitliği bozuk: Σborç=" + debit + ", Σalacak=" + credit);
        }
    }
}
