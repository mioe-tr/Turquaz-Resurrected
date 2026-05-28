/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.accounting.AccountingDtos.JournalLineRequest;
import com.turquaz.api.accounting.AccountingDtos.PostJournalRequest;
import com.turquaz.api.support.AuthenticatedTest;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AccountingControllerTest extends AuthenticatedTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired ModuleRepository moduleRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired AccountingJournalRepository journalRepo;
    @Autowired AccountingTransactionTypeRepository txTypeRepo;
    @Autowired AccountingAccountRepository accountRepo;

    private UUID journalId;
    private UUID txTypeId;
    private UUID moduleId;
    private UUID sequenceId;
    private UUID exchangeRateId;
    private UUID accountA;
    private UUID accountB;

    @BeforeEach
    void setUp() {
        initAuth("acc-ctrl");
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
    }

    @Test
    void dengeli_fiş_post_201_döner() throws Exception {
        PostJournalRequest req = new PostJournalRequest(
                LocalDate.of(2026, 4, 1), "API-1", "Test fiş",
                journalId, txTypeId, moduleId, sequenceId,
                exchangeRateId, BigDecimal.ONE,
                List.of(
                        new JournalLineRequest(accountA, new BigDecimal("100"), BigDecimal.ZERO, "borç"),
                        new JournalLineRequest(accountB, BigDecimal.ZERO, new BigDecimal("100"), "alacak")));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounting/journal")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.documentNo").value("API-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").isNotEmpty());
    }

    @Test
    void dengesiz_fiş_422_döner() throws Exception {
        PostJournalRequest req = new PostJournalRequest(
                LocalDate.of(2026, 4, 1), "API-BAD", "Dengesiz",
                journalId, txTypeId, moduleId, sequenceId,
                exchangeRateId, BigDecimal.ONE,
                List.of(
                        new JournalLineRequest(accountA, new BigDecimal("100"), BigDecimal.ZERO, "x"),
                        new JournalLineRequest(accountB, BigDecimal.ZERO, new BigDecimal("99"), "y")));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounting/journal")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("eşit değil")));
    }

    @Test
    void hesap_bakiyesi_endpoint_döner() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(
                        "/api/v1/accounting/accounts/" + accountA + "/balance")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountId").value(accountA.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalDebit").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalCredit").exists());
    }

    @Test
    void mizan_endpoint_liste_döner() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/accounting/trial-balance")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void boş_satır_listesi_validation_400() throws Exception {
        String body = """
                {"transactionDate":"2026-04-01","documentNo":"X","description":"x",
                 "journalId":"%s","transactionTypeId":"%s","moduleId":"%s",
                 "engineSequenceId":"%s","exchangeRateId":"%s","exchangeRate":1.0,
                 "lines":[]}
                """.formatted(journalId, txTypeId, moduleId, sequenceId, exchangeRateId);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounting/journal")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
