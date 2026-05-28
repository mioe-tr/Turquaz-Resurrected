/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.accounting;

import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Yevmiye fişi postlama servisi: bir {@link JournalEntry}'i bir
 * {@code TurqAccountingTransaction} ve N {@code TurqAccountingTransactionColumn}
 * satırı olarak kalıcılaştırır.
 *
 * <p>{@code JournalEntry}'nin kendi constructor'ı Σborç=Σalacak invariant'ını
 * zaten zorlar — bu servise dengesiz bir fiş gelmez. Kur çevirisi
 * {@link PostingContext#exchangeRate()} ile {@code rows_dept_in_base_currency}
 * ve {@code rows_credit_in_base_currency}'ye yansıtılır.
 *
 * <p>Eski {@code com.turquaz.accounting.bl.AccBLTransactionAdd} sınıfının modern
 * yeniden yazımı: {@code HashMap argMap} yerine kayıt tipleri, manuel SQL/JTA
 * yerine Spring {@code @Transactional}.
 */
@Service
public class JournalService {

    private final AccountingTransactionRepository txRepo;
    private final AccountingTransactionColumnRepository colRepo;

    public JournalService(
            AccountingTransactionRepository txRepo,
            AccountingTransactionColumnRepository colRepo) {
        this.txRepo = txRepo;
        this.colRepo = colRepo;
    }

    @Transactional
    public AccountingTransaction post(JournalEntry entry, PostingContext ctx) {
        AccountingTransaction tx = new AccountingTransaction();
        tx.setCompanyId(ctx.companyId());
        tx.setAccountingJournalId(ctx.journalId());
        tx.setAccountingTransactionTypesId(ctx.transactionTypeId());
        tx.setModuleId(ctx.moduleId());
        tx.setEngineSequencesId(ctx.engineSequenceId());
        tx.setExchangeRateId(ctx.exchangeRateId());
        tx.setTransactionsDate(entry.transactionDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        tx.setTransactionDocumentNo(entry.documentNo());
        tx.setTransactionDescription(entry.description());
        tx.setCreatedBy(ctx.currentUser());
        tx.setUpdatedBy(ctx.currentUser());
        txRepo.save(tx);

        for (JournalLine line : entry.lines()) {
            AccountingTransactionColumn col = new AccountingTransactionColumn();
            col.setCompanyId(ctx.companyId());
            col.setAccountingAccountsId(line.accountId());
            col.setAccountingTransactionsId(tx.getId());
            col.setDeptAmount(line.debit().amount());
            col.setCreditAmount(line.credit().amount());
            col.setTransactionDefinition(line.description());
            col.setRowsDeptInBaseCurrency(line.debit().toBaseCurrency(ctx.exchangeRate()).amount());
            col.setRowsCreditInBaseCurrency(line.credit().toBaseCurrency(ctx.exchangeRate()).amount());
            col.setExchangeRateId(ctx.exchangeRateId());
            col.setCreatedBy(ctx.currentUser());
            col.setUpdatedBy(ctx.currentUser());
            colRepo.save(col);
        }
        return tx;
    }
}
