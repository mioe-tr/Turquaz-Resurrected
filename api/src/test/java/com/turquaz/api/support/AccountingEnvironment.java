/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.support;

import com.turquaz.persistence.accounting.AccountingAccountRepository;
import com.turquaz.persistence.accounting.AccountingJournal;
import com.turquaz.persistence.accounting.AccountingJournalRepository;
import com.turquaz.persistence.accounting.AccountingTransactionType;
import com.turquaz.persistence.accounting.AccountingTransactionTypeRepository;
import com.turquaz.persistence.common.Currency;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import com.turquaz.persistence.engine.EngineSequence;
import com.turquaz.persistence.engine.EngineSequenceRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yevmiye postlamayı gerektiren controller testleri için ortak FK kurulum
 * yardımcısı: journal, transaction type, sequence, currency, exchange rate,
 * module ve test hesapları üretir.
 *
 * <p>{@code @Component} olarak inject edilebilir; her test sınıfında bir kez
 * setUp metodu çağrılarak {@link #ensureFor} ile ortam kurulur.
 */
@Component
public class AccountingEnvironment {

    @Autowired ModuleRepository moduleRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired AccountingJournalRepository journalRepo;
    @Autowired AccountingTransactionTypeRepository txTypeRepo;
    @Autowired AccountingAccountRepository accountRepo;

    public UUID moduleId;
    public UUID journalId;
    public UUID txTypeId;
    public UUID sequenceId;
    public UUID currencyId;
    public UUID exchangeRateId;
    public BigDecimal exchangeRate = BigDecimal.ONE;
    public UUID accountA;
    public UUID accountB;
    public UUID accountC;

    public void ensureFor(UUID companyId) {
        moduleId = moduleRepo.findAll().getFirst().getId();

        Currency cur = currencyRepo.findAll().stream().findFirst().orElseGet(() -> {
            Currency c = new Currency();
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
        currencyId = cur.getId();

        CurrencyExchangeRate r = new CurrencyExchangeRate();
        r.setCompanyId(companyId);
        r.setBaseCurrencyId(cur.getId());
        r.setExchangeCurrencyId(cur.getId());
        r.setExchangeRatio(new BigDecimal("1.000000"));
        r.setExhangeRatesDate(Instant.now());
        exchangeRateId = rateRepo.save(r).getId();

        EngineSequence s = new EngineSequence();
        s.setCompanyId(companyId);
        s.setModulesId(moduleId);
        sequenceId = sequenceRepo.save(s).getId();

        AccountingJournal j = new AccountingJournal();
        j.setCompanyId(companyId);
        j.setJournalDate(Instant.now());
        j.setCreatedBy("test");
        j.setUpdatedBy("test");
        journalId = journalRepo.save(j).getId();

        AccountingTransactionType tt = new AccountingTransactionType();
        tt.setCompanyId(companyId);
        tt.setTypesName("Test");
        tt.setCreatedBy("test");
        tt.setUpdatedBy("test");
        txTypeId = txTypeRepo.save(tt).getId();

        var accounts = accountRepo.findAll();
        accountA = accounts.get(0).getId();
        accountB = accounts.get(1).getId();
        accountC = accounts.get(2).getId();
    }
}
