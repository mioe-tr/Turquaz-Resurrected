/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.cash;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.accounting.JournalService;
import com.turquaz.persistence.accounting.PostingContext;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kasa hareketi servisi. Eski {@code CashBLCashTransactionAdd}'in modern
 * karşılığı.
 *
 * <p>Yapı: bir kasa hareketi 1 başlık ({@link CashTransaction}) + N satır
 * ({@link CashTransactionRow}) — her satır bir karşı hesap, borç veya alacak
 * tutarı taşır. {@link JournalEntry}'e dönüştürülüp post edilir; çift taraplı
 * invariant satır listesinin construct'inde zorlanır.
 */
@Service
public class CashTransactionService {

    private final CashTransactionRepository txRepo;
    private final CashTransactionRowRepository rowRepo;
    private final CashCardRepository cashCardRepo;
    private final JournalService journalService;

    public CashTransactionService(
            CashTransactionRepository txRepo,
            CashTransactionRowRepository rowRepo,
            CashCardRepository cashCardRepo,
            JournalService journalService) {
        this.txRepo = txRepo;
        this.rowRepo = rowRepo;
        this.cashCardRepo = cashCardRepo;
        this.journalService = journalService;
    }

    @Transactional
    public AccountingTransaction record(CashMovement m) {
        if (m.rows().isEmpty()) {
            throw new BusinessRuleException("Kasa hareketi en az bir satır gerektirir");
        }
        CashCard cashCard = cashCardRepo.findById(m.cashCardId())
                .orElseThrow(() -> new BusinessRuleException(
                        "Kasa kartı bulunamadı: " + m.cashCardId()));

        CashTransaction header = new CashTransaction();
        header.setCompanyId(m.context().companyId());
        header.setCashTransactionsTypesId(m.cashTransactionTypeId());
        header.setEngineSequencesId(m.context().engineSequenceId());
        header.setTransactionDate(m.date().atStartOfDay(ZoneOffset.UTC).toInstant());
        header.setTransactionDefinition(m.description());
        header.setDocumentNo(m.documentNo());
        header.setCreatedBy(m.context().currentUser());
        header.setUpdatedBy(m.context().currentUser());
        txRepo.save(header);

        // Yevmiye satırları: her kasa hareket satırı bir karşı hesap (debit/credit) +
        // karşılığında kasa muhasebe hesabı toplam debit/credit'i.
        List<JournalLine> journalLines = new ArrayList<>();
        Money cashDebit = Money.ZERO;
        Money cashCredit = Money.ZERO;

        for (CashRow r : m.rows()) {
            CashTransactionRow row = new CashTransactionRow();
            row.setCompanyId(m.context().companyId());
            row.setCashTransactionsId(header.getId());
            row.setCashCardsId(m.cashCardId());
            row.setAccountingAccountsId(r.counterAccountId());
            row.setDeptAmount(r.debit().amount());
            row.setCreditAmount(r.credit().amount());
            row.setTransactionDefinition(r.description());
            Money foreignD = r.debit().toBaseCurrency(m.context().exchangeRate());
            Money foreignC = r.credit().toBaseCurrency(m.context().exchangeRate());
            row.setDeptAmountInForeignCurrency(foreignD.amount());
            row.setCreditAmountInForeignCurrency(foreignC.amount());
            row.setExchangeRateId(m.context().exchangeRateId());
            row.setCreatedBy(m.context().currentUser());
            row.setUpdatedBy(m.context().currentUser());
            rowRepo.save(row);

            // Kasa, karşı tarafın tersi yönde etkilenir (klasik kontra hesap)
            cashCredit = cashCredit.add(r.debit());
            cashDebit = cashDebit.add(r.credit());

            // Karşı taraf için yevmiye satırı: r'in kendi yönüyle
            if (!r.debit().isZero()) {
                journalLines.add(JournalLine.debit(r.counterAccountId(), r.debit(), r.description()));
            }
            if (!r.credit().isZero()) {
                journalLines.add(JournalLine.credit(r.counterAccountId(), r.credit(), r.description()));
            }
        }

        // Kasa tarafının net etkisini tek satır olarak ekle (klasik özet)
        if (!cashDebit.isZero()) {
            journalLines.add(JournalLine.debit(
                    cashCard.getAccountingAccountsId(), cashDebit, "Kasa girişi"));
        }
        if (!cashCredit.isZero()) {
            journalLines.add(JournalLine.credit(
                    cashCard.getAccountingAccountsId(), cashCredit, "Kasa çıkışı"));
        }

        JournalEntry entry = new JournalEntry(
                m.date(), m.documentNo(), m.description(), journalLines);
        return journalService.post(entry, m.context());
    }

    /** Kasa hareketi satırı (karşı hesaba göre borç veya alacak). XOR: tek taraf. */
    public record CashRow(UUID counterAccountId, Money debit, Money credit, String description) {
        public CashRow {
            if (debit == null || credit == null) {
                throw new BusinessRuleException("Borç/alacak null olamaz");
            }
            if (debit.isNegative() || credit.isNegative()) {
                throw new BusinessRuleException("Borç/alacak negatif olamaz");
            }
            if (!debit.isZero() && !credit.isZero()) {
                throw new BusinessRuleException(
                        "Kasa satırı aynı anda hem borç hem alacak içeremez");
            }
            if (debit.isZero() && credit.isZero()) {
                throw new BusinessRuleException("Kasa satırı boş olamaz");
            }
        }
    }

    /** Kasa hareketi komutu. */
    public record CashMovement(
            UUID cashCardId,
            UUID cashTransactionTypeId,
            LocalDate date,
            String documentNo,
            String description,
            List<CashRow> rows,
            PostingContext context) {
    }
}
