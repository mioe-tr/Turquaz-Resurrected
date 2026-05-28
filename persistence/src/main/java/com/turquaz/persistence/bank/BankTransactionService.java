/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bank;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.accounting.JournalService;
import com.turquaz.persistence.accounting.PostingContext;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Banka hareketi servisi. Hareket DB'ye yazıldığı GİBİ otomatik olarak ilgili
 * yevmiye fişini de oluşturur (çift taraflı entegrasyon).
 *
 * <p>Eski {@code BankBLTransactionAdd}'in modern karşılığı: HashMap argMap +
 * manual session yerine {@link BankMovement} record + Spring {@code @Transactional}.
 * Çift taraplı invariant {@link JournalEntry} constructor'ında zorlanır.
 */
@Service
public class BankTransactionService {

    private final BanksTransactionRepository txRepo;
    private final JournalService journalService;

    public BankTransactionService(
            BanksTransactionRepository txRepo,
            JournalService journalService) {
        this.txRepo = txRepo;
        this.journalService = journalService;
    }

    /** Bankaya para girişi: banka hesabı borçlandırılır, karşı taraf alacaklandırılır. */
    @Transactional
    public AccountingTransaction recordDeposit(BankMovement m) {
        return record(m, true);
    }

    /** Bankadan para çıkışı: karşı taraf borçlandırılır, banka hesabı alacaklandırılır. */
    @Transactional
    public AccountingTransaction recordWithdrawal(BankMovement m) {
        return record(m, false);
    }

    private AccountingTransaction record(BankMovement m, boolean deposit) {
        if (m.amount().isZero() || m.amount().isNegative()) {
            throw new BusinessRuleException("Banka hareket tutarı pozitif olmalı: " + m.amount());
        }

        BanksTransaction tx = new BanksTransaction();
        tx.setCompanyId(m.context().companyId());
        tx.setBanksCardsId(m.bankCardId());
        tx.setBankTransactionsBillsId(m.bankTransactionsBillsId());
        tx.setExchangeRateId(m.context().exchangeRateId());
        // Eski şema banka satırında dept/credit'i toplar. Giriş ise debit (asset ↑),
        // çıkış ise credit (asset ↓).
        tx.setDeptAmount(deposit ? m.amount().amount() : BigDecimal.ZERO);
        tx.setCreditAmount(deposit ? BigDecimal.ZERO : m.amount().amount());
        Money foreign = m.amount().toBaseCurrency(m.context().exchangeRate());
        tx.setDeptAmountInForeignCurrency(deposit ? foreign.amount() : BigDecimal.ZERO);
        tx.setCreditAmountInForeignCurrency(deposit ? BigDecimal.ZERO : foreign.amount());
        tx.setCreatedBy(m.context().currentUser());
        tx.setUpdatedBy(m.context().currentUser());
        txRepo.save(tx);

        JournalLine bankLine = deposit
                ? JournalLine.debit(m.bankAccountingAccountId(), m.amount(), m.description())
                : JournalLine.credit(m.bankAccountingAccountId(), m.amount(), m.description());
        JournalLine counterLine = deposit
                ? JournalLine.credit(m.counterAccountId(), m.amount(), m.description())
                : JournalLine.debit(m.counterAccountId(), m.amount(), m.description());

        JournalEntry entry = new JournalEntry(
                m.date(), m.documentNo(), m.description(),
                List.of(bankLine, counterLine));
        return journalService.post(entry, m.context());
    }
}
