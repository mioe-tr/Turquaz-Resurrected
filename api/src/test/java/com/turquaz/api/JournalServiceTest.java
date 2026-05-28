/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.accounting.AccountingException;
import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingJournal;
import com.turquaz.persistence.accounting.AccountingJournalRepository;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.accounting.AccountingTransactionColumnRepository;
import com.turquaz.persistence.accounting.AccountingTransactionRepository;
import com.turquaz.persistence.accounting.AccountingTransactionType;
import com.turquaz.persistence.accounting.AccountingTransactionTypeRepository;
import com.turquaz.persistence.accounting.JournalService;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import com.turquaz.persistence.engine.EngineSequence;
import com.turquaz.persistence.engine.EngineSequenceRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * {@link JournalService} entegrasyon testi: gerçek PostgreSQL'e karşı, seed
 * edilmiş hesap planını kullanarak yevmiye fişi postlar ve veritabanı
 * durumunu doğrular.
 */
@SpringBootTest
class JournalServiceTest {

    @Autowired JournalService journalService;
    @Autowired AccountingTransactionRepository txRepo;
    @Autowired AccountingTransactionColumnRepository colRepo;
    @Autowired AccountingJournalRepository journalRepo;
    @Autowired AccountingTransactionTypeRepository txTypeRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired ModuleRepository moduleRepo;
    @Autowired CompanyRepository companyRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired
    com.turquaz.persistence.accounting.AccountingAccountRepository accountRepo;

    private PostingContext ctx;
    private UUID kasaAccountId;
    private UUID gelirAccountId;

    @BeforeEach
    void setUp() {
        UUID companyId = companyRepo.findAll().getFirst().getId();
        UUID moduleId = moduleRepo.findAll().getFirst().getId();
        // Seed'lenmiş hesap planından iki ayrı hesap seç (461 hesap var)
        var accounts = accountRepo.findAll();
        kasaAccountId = accounts.get(0).getId();
        gelirAccountId = accounts.get(1).getId();

        AccountingJournal journal = new AccountingJournal();
        journal.setCompanyId(companyId);
        journal.setJournalDate(Instant.now());
        journal.setCreatedBy("test");
        journal.setUpdatedBy("test");
        journalRepo.save(journal);

        AccountingTransactionType txType = new AccountingTransactionType();
        txType.setCompanyId(companyId);
        txType.setTypesName("Test");
        txType.setCreatedBy("test");
        txType.setUpdatedBy("test");
        txTypeRepo.save(txType);

        EngineSequence sequence = new EngineSequence();
        sequence.setCompanyId(companyId);
        sequence.setModulesId(moduleId);
        sequenceRepo.save(sequence);

        var currency = currencyRepo.findAll().stream().findFirst().orElseGet(() -> {
            var c = new com.turquaz.persistence.common.Currency();
            c.setCompanyId(companyId);
            c.setCurrenciesName("TL");
            c.setCurrenciesAbbreviation("TL");
            c.setCurrenciesCountry("TR");
            c.setDefaultCurrency(true);
            c.setConstant(true);
            c.setCreatedBy("test");
            c.setUpdatedBy("test");
            return currencyRepo.save(c);
        });

        CurrencyExchangeRate rate = new CurrencyExchangeRate();
        rate.setCompanyId(companyId);
        rate.setBaseCurrencyId(currency.getId());
        rate.setExchangeCurrencyId(currency.getId());
        rate.setExchangeRatio(new BigDecimal("1.000000"));
        rate.setExhangeRatesDate(Instant.now());
        rateRepo.save(rate);

        ctx = new PostingContext(
                companyId,
                journal.getId(),
                txType.getId(),
                moduleId,
                sequence.getId(),
                rate.getId(),
                new BigDecimal("1.0"),
                "test");
    }

    @Test
    void dengeli_fiş_postlanır_ve_satırlar_oluşur() {
        long colsBefore = colRepo.count();

        JournalEntry entry = new JournalEntry(
                LocalDate.of(2026, 5, 28),
                "TEST-001",
                "Nakit satış",
                List.of(
                        JournalLine.debit(kasaAccountId, Money.of("1000"), "Kasa girişi"),
                        JournalLine.credit(gelirAccountId, Money.of("1000"), "Satış geliri")));

        AccountingTransaction posted = journalService.post(entry, ctx);

        assertThat(posted.getId()).isNotNull();
        assertThat(txRepo.findById(posted.getId())).isPresent();
        assertThat(colRepo.count()).isEqualTo(colsBefore + 2);
    }

    @Test
    void dengesiz_fiş_DB_ye_gitmeden_reddedilir() {
        // JournalEntry constructor invariant'ı erken yakalar — DB'ye hiç gidilmez
        long txBefore = txRepo.count();
        assertThatThrownBy(() -> new JournalEntry(
                LocalDate.now(),
                "TEST-BAD",
                "Dengesiz",
                List.of(
                        JournalLine.debit(kasaAccountId, Money.of("100"), "x"),
                        JournalLine.credit(gelirAccountId, Money.of("99"), "y"))))
                .isInstanceOf(AccountingException.class);
        assertThat(txRepo.count()).isEqualTo(txBefore);
    }

    @Test
    void kur_çevirisi_base_currency_sütunlarına_yansır() {
        PostingContext usdCtx = new PostingContext(
                ctx.companyId(),
                ctx.journalId(),
                ctx.transactionTypeId(),
                ctx.moduleId(),
                ctx.engineSequenceId(),
                ctx.exchangeRateId(),
                new BigDecimal("40.50"), // 1 USD = 40.50 TL
                ctx.currentUser());

        JournalEntry entry = new JournalEntry(
                LocalDate.now(),
                "TEST-USD",
                "Dövizli işlem",
                List.of(
                        JournalLine.debit(kasaAccountId, Money.of("100"), "USD"),
                        JournalLine.credit(gelirAccountId, Money.of("100"), "USD")));

        AccountingTransaction posted = journalService.post(entry, usdCtx);
        var cols = colRepo.findAll().stream()
                .filter(c -> c.getAccountingTransactionsId().equals(posted.getId()))
                .toList();
        assertThat(cols).hasSize(2);
        var debitCol = cols.stream().filter(c -> c.getDeptAmount().signum() > 0).findFirst().orElseThrow();
        assertThat(debitCol.getRowsDeptInBaseCurrency())
                .isEqualByComparingTo(new BigDecimal("4050.0000"));
    }
}
