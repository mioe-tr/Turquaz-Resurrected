/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import com.turquaz.core.money.Money;
import com.turquaz.core.money.Quantity;
import com.turquaz.persistence.accounting.AccountingAccountRepository;
import com.turquaz.persistence.accounting.AccountingJournal;
import com.turquaz.persistence.accounting.AccountingJournalRepository;
import com.turquaz.persistence.accounting.AccountingTransactionType;
import com.turquaz.persistence.accounting.AccountingTransactionTypeRepository;
import com.turquaz.persistence.accounting.JournalService;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.accounting.TrialBalanceLine;
import com.turquaz.persistence.accounting.TrialBalanceService;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.common.Currency;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import com.turquaz.persistence.engine.EngineSequence;
import com.turquaz.persistence.engine.EngineSequenceRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import com.turquaz.persistence.inventory.CardProfit;
import com.turquaz.persistence.inventory.InventoryCard;
import com.turquaz.persistence.inventory.InventoryCardService;
import com.turquaz.persistence.inventory.InventoryProfitService;
import com.turquaz.persistence.inventory.InventoryTransaction;
import com.turquaz.persistence.inventory.InventoryTransactionRepository;
import com.turquaz.persistence.inventory.InventoryTransactionType;
import com.turquaz.persistence.inventory.InventoryTransactionTypeRepository;
import com.turquaz.persistence.inventory.InventoryUnit;
import com.turquaz.persistence.inventory.InventoryUnitRepository;
import com.turquaz.persistence.inventory.InventoryWarehouse;
import com.turquaz.persistence.inventory.InventoryWarehouseService;
import com.turquaz.persistence.inventory.NewInventoryCard;
import com.turquaz.persistence.current.CurrentCard;
import com.turquaz.persistence.current.CurrentCardService;
import com.turquaz.persistence.current.NewCurrentCard;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportingServicesTest {

    @Autowired CompanyRepository companies;
    @Autowired ModuleRepository moduleRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired AccountingJournalRepository journalRepo;
    @Autowired AccountingTransactionTypeRepository txTypeRepo;
    @Autowired AccountingAccountRepository accountRepo;

    @Autowired JournalService journalService;
    @Autowired TrialBalanceService trialBalanceService;

    @Autowired InventoryUnitRepository unitRepo;
    @Autowired InventoryTransactionTypeRepository invTxTypeRepo;
    @Autowired InventoryTransactionRepository invTxRepo;
    @Autowired InventoryCardService invCardService;
    @Autowired InventoryWarehouseService warehouseService;
    @Autowired InventoryProfitService profitService;
    @Autowired CurrentCardService currentCardService;

    private PostingContext ctx;
    private UUID companyId;
    private UUID accountA;
    private UUID accountB;
    private UUID inventoryUnitId;
    private UUID inventoryTxTypeId;
    private UUID currentCardId;
    private UUID warehouseId;
    private UUID rateId;
    private UUID sequenceId;

    @BeforeEach
    void setUp() {
        companyId = companies.findAll().getFirst().getId();
        UUID moduleId = moduleRepo.findAll().getFirst().getId();

        Currency currency = currencyRepo.findAll().stream().findFirst().orElseGet(() -> {
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

        CurrencyExchangeRate rate = new CurrencyExchangeRate();
        rate.setCompanyId(companyId);
        rate.setBaseCurrencyId(currency.getId());
        rate.setExchangeCurrencyId(currency.getId());
        rate.setExchangeRatio(new BigDecimal("1.000000"));
        rate.setExhangeRatesDate(Instant.now());
        rateId = rateRepo.save(rate).getId();

        EngineSequence seq = new EngineSequence();
        seq.setCompanyId(companyId);
        seq.setModulesId(moduleId);
        sequenceId = sequenceRepo.save(seq).getId();

        AccountingJournal j = new AccountingJournal();
        j.setCompanyId(companyId);
        j.setJournalDate(Instant.now());
        j.setCreatedBy("test");
        j.setUpdatedBy("test");
        UUID journalId = journalRepo.save(j).getId();

        AccountingTransactionType txType = new AccountingTransactionType();
        txType.setCompanyId(companyId);
        txType.setTypesName("Test");
        txType.setCreatedBy("test");
        txType.setUpdatedBy("test");
        UUID txTypeId = txTypeRepo.save(txType).getId();

        var accounts = accountRepo.findAll();
        accountA = accounts.get(0).getId();
        accountB = accounts.get(1).getId();

        ctx = new PostingContext(
                companyId, journalId, txTypeId, moduleId, sequenceId,
                rateId, new BigDecimal("1.0"), "test");

        // Stok testleri için ek alt yapı
        InventoryUnit unit = new InventoryUnit();
        unit.setCompanyId(companyId);
        unit.setUnitsName("Adet");
        unit.setCreatedBy("test");
        unit.setUpdatedBy("test");
        inventoryUnitId = unitRepo.save(unit).getId();

        InventoryTransactionType ttype = new InventoryTransactionType();
        ttype.setCompanyId(companyId);
        ttype.setTypeName("Test");
        ttype.setCreatedBy("test");
        ttype.setUpdatedBy("test");
        inventoryTxTypeId = invTxTypeRepo.save(ttype).getId();

        CurrentCard cc = currentCardService.create(new NewCurrentCard(
                companyId, "CC-R-" + System.nanoTime(),
                "Test", "x", "y", "VD", "0",
                Money.ZERO, Money.ZERO, BigDecimal.ZERO, Money.ZERO, 0, "test"));
        currentCardId = cc.getId();

        InventoryWarehouse w = warehouseService.create(
                companyId, "DEP-R-" + System.nanoTime(),
                "Test", "a", "i", "0", "d", "test");
        warehouseId = w.getId();
    }

    // -------------------- Mizan + hesap bakiyesi --------------------

    @Test
    void mizan_postlanan_fişin_etkisini_yansıtır() {
        // Bu test sınıfı paylaşılan seed hesap planını kullanır; başka testlerin
        // postlamaları kalıcı kalabilir → mutlak değer yerine delta doğrularız.
        Instant asOf = Instant.parse("2026-12-31T23:59:59Z");
        TrialBalanceLine aBefore = trialBalanceService.accountBalance(companyId, accountA, asOf);
        TrialBalanceLine bBefore = trialBalanceService.accountBalance(companyId, accountB, asOf);

        // İki dengeli fiş: A'ya 1250 borç, B'ye 1250 alacak.
        journalService.post(new JournalEntry(
                LocalDate.of(2026, 1, 15), "M-1", "Test 1",
                List.of(
                        JournalLine.debit(accountA, Money.of("1000"), "x"),
                        JournalLine.credit(accountB, Money.of("1000"), "x"))), ctx);
        journalService.post(new JournalEntry(
                LocalDate.of(2026, 2, 15), "M-2", "Test 2",
                List.of(
                        JournalLine.debit(accountA, Money.of("250"), "y"),
                        JournalLine.credit(accountB, Money.of("250"), "y"))), ctx);

        TrialBalanceLine aAfter = trialBalanceService.accountBalance(companyId, accountA, asOf);
        TrialBalanceLine bAfter = trialBalanceService.accountBalance(companyId, accountB, asOf);

        assertThat(aAfter.totalDebit().subtract(aBefore.totalDebit())).isEqualTo(Money.of("1250"));
        assertThat(aAfter.totalCredit().subtract(aBefore.totalCredit())).isEqualTo(Money.ZERO);
        assertThat(bAfter.totalCredit().subtract(bBefore.totalCredit())).isEqualTo(Money.of("1250"));
        assertThat(bAfter.totalDebit().subtract(bBefore.totalDebit())).isEqualTo(Money.ZERO);
    }

    @Test
    void mizan_tarih_kesiti_yalnız_dahil_fişleri_sayar() {
        Instant midpoint = Instant.parse("2026-03-31T23:59:59Z");
        TrialBalanceLine aBefore = trialBalanceService.accountBalance(companyId, accountA, midpoint);

        journalService.post(new JournalEntry(
                LocalDate.of(2026, 1, 15), "C-1", "Erken",
                List.of(
                        JournalLine.debit(accountA, Money.of("500"), "x"),
                        JournalLine.credit(accountB, Money.of("500"), "y"))), ctx);
        journalService.post(new JournalEntry(
                LocalDate.of(2026, 6, 15), "C-2", "Geç",
                List.of(
                        JournalLine.debit(accountA, Money.of("700"), "x"),
                        JournalLine.credit(accountB, Money.of("700"), "y"))), ctx);

        // Mart sonu itibarıyla yalnız ilk fiş dahil → A'nın borcu +500 artmalı
        TrialBalanceLine aAfter = trialBalanceService.accountBalance(companyId, accountA, midpoint);
        assertThat(aAfter.totalDebit().subtract(aBefore.totalDebit())).isEqualTo(Money.of("500"));
    }

    @Test
    void defter_eşitliği_doğrulanır() {
        journalService.post(new JournalEntry(
                LocalDate.of(2026, 1, 1), "EQ", "Denge",
                List.of(
                        JournalLine.debit(accountA, Money.of("999.50"), "x"),
                        JournalLine.credit(accountB, Money.of("999.50"), "y"))), ctx);

        // İhlal yoksa exception atmamalı
        trialBalanceService.ensureBooksBalance(
                companyId, Instant.parse("2026-12-31T23:59:59Z"));
    }

    // -------------------- Stok kâr analizi --------------------

    @Test
    void kâr_analizi_ortalama_maliyetle_hesaplanır() {
        // Bir kart için: 100 birim giriş × 10 = 1000 maliyet
        //              50 birim çıkış × 15 = 750 gelir
        // Ortalama maliyet: 10. Satılanın maliyeti: 50 × 10 = 500. Kâr: 250.
        InventoryCard card = invCardService.create(new NewInventoryCard(
                companyId, "STK-P-" + System.nanoTime(),
                "Kâr testi", "x", 0, 1000, 18, 0, 0, "test"));

        Instant t1 = Instant.parse("2026-01-10T10:00:00Z");
        Instant t2 = Instant.parse("2026-01-20T10:00:00Z");
        saveTx(card.getId(), Quantity.of("100"), Quantity.ZERO,
                Money.of("10"), Money.of("1000"), t1);
        saveTx(card.getId(), Quantity.ZERO, Quantity.of("50"),
                Money.of("15"), Money.of("750"), t2);

        List<CardProfit> report = profitService.report(
                companyId,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-12-31T23:59:59Z"));
        CardProfit p = report.stream().filter(r -> r.cardId().equals(card.getId()))
                .findFirst().orElseThrow();

        assertThat(p.amountIn()).isEqualTo(Quantity.of("100"));
        assertThat(p.amountOut()).isEqualTo(Quantity.of("50"));
        assertThat(p.costIn()).isEqualTo(Money.of("1000"));
        assertThat(p.revenueOut()).isEqualTo(Money.of("750"));
        assertThat(p.avgUnitCost()).isEqualTo(Money.of("10"));
        assertThat(p.costOfSold()).isEqualTo(Money.of("500"));
        assertThat(p.profit()).isEqualTo(Money.of("250"));
    }

    @Test
    void giriş_yoksa_ortalama_maliyet_sıfır() {
        // Yalnız çıkış olan (örn. açılış stoğu kaydı yoksa) → maliyet sıfır,
        // tüm gelir kâr olarak görünür.
        InventoryCard card = invCardService.create(new NewInventoryCard(
                companyId, "STK-Z-" + System.nanoTime(),
                "Sadece çıkış", "x", 0, 1000, 18, 0, 0, "test"));
        saveTx(card.getId(), Quantity.ZERO, Quantity.of("10"),
                Money.of("20"), Money.of("200"), Instant.parse("2026-01-15T10:00:00Z"));

        CardProfit p = profitService.report(
                        companyId,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-12-31T23:59:59Z"))
                .stream().filter(r -> r.cardId().equals(card.getId())).findFirst().orElseThrow();

        assertThat(p.avgUnitCost()).isEqualTo(Money.ZERO);
        assertThat(p.costOfSold()).isEqualTo(Money.ZERO);
        assertThat(p.profit()).isEqualTo(Money.of("200"));
    }

    // -------------------- yardımcılar --------------------

    private void saveTx(UUID cardId, Quantity in, Quantity out,
                        Money unitPrice, Money totalPrice, Instant at) {
        InventoryTransaction t = new InventoryTransaction();
        t.setCompanyId(companyId);
        t.setInventoryCardsId(cardId);
        t.setInventoryWarehousesId(warehouseId);
        t.setEngineSequencesId(sequenceId);
        t.setInventoryUnitsId(inventoryUnitId);
        t.setExchangeRateId(rateId);
        t.setCurrentCardsId(currentCardId);
        t.setTransactionType(inventoryTxTypeId);
        t.setAmountIn(in.value());
        t.setAmountOut(out.value());
        t.setUnitPrice(unitPrice.amount());
        t.setTotalPrice(totalPrice.amount());
        t.setDiscountRate(BigDecimal.ZERO);
        t.setDiscountAmount(BigDecimal.ZERO);
        t.setVatAmount(BigDecimal.ZERO);
        t.setVatRate(BigDecimal.ZERO);
        t.setVatSpecialUnitPrice(BigDecimal.ZERO);
        t.setVatSpecialRate(BigDecimal.ZERO);
        t.setVatSpecialAmount(BigDecimal.ZERO);
        t.setCumilativePrice(BigDecimal.ZERO);
        t.setUnitPriceInForeignCurrency(BigDecimal.ZERO);
        t.setTotalPriceInForeignCurrency(BigDecimal.ZERO);
        t.setDiscountAmountInForeignCurrency(BigDecimal.ZERO);
        t.setVatAmountInForeignCurrency(BigDecimal.ZERO);
        t.setVatSpecialUnitPriceInForeignCurrency(BigDecimal.ZERO);
        t.setVatSpecialAmountInForeignCurrency(BigDecimal.ZERO);
        t.setCumilativePriceInForeignCurrency(BigDecimal.ZERO);
        t.setTransactionsDate(at);
        t.setDocumentNo("TEST");
        t.setDefinition("test");
        t.setCreatedBy("test");
        t.setUpdatedBy("test");
        invTxRepo.save(t);
    }
}
