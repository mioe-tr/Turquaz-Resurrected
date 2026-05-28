/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Quantity;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.common.Currency;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import com.turquaz.persistence.current.CurrentCard;
import com.turquaz.persistence.current.CurrentCardService;
import com.turquaz.persistence.current.NewCurrentCard;
import com.turquaz.persistence.engine.EngineSequence;
import com.turquaz.persistence.engine.EngineSequenceRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import com.turquaz.persistence.inventory.InventoryCard;
import com.turquaz.persistence.inventory.InventoryCardService;
import com.turquaz.persistence.inventory.InventoryLedgerService;
import com.turquaz.persistence.inventory.InventoryTransaction;
import com.turquaz.persistence.inventory.InventoryTransactionRepository;
import com.turquaz.persistence.inventory.InventoryTransactionType;
import com.turquaz.persistence.inventory.InventoryTransactionTypeRepository;
import com.turquaz.persistence.inventory.InventoryUnit;
import com.turquaz.persistence.inventory.InventoryUnitRepository;
import com.turquaz.persistence.inventory.InventoryWarehouse;
import com.turquaz.persistence.inventory.InventoryWarehouseService;
import com.turquaz.persistence.inventory.NewInventoryCard;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryServicesTest {

    @Autowired CompanyRepository companies;
    @Autowired ModuleRepository moduleRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired InventoryUnitRepository unitRepo;
    @Autowired InventoryTransactionTypeRepository txTypeRepo;

    @Autowired InventoryCardService cardService;
    @Autowired InventoryWarehouseService warehouseService;
    @Autowired InventoryLedgerService ledger;
    @Autowired InventoryTransactionRepository txRepo;
    @Autowired CurrentCardService currentCardService;

    private UUID companyId;
    private UUID sequenceId;
    private UUID unitId;
    private UUID exchangeRateId;
    private UUID txTypeId;
    private UUID currentCardId;

    @BeforeEach
    void setUp() {
        companyId = companies.findAll().getFirst().getId();
        UUID moduleId = moduleRepo.findAll().getFirst().getId();

        Currency currency = currencyRepo.findAll().stream().findFirst().orElseGet(() -> {
            var c = new Currency();
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
        exchangeRateId = rateRepo.save(rate).getId();

        EngineSequence seq = new EngineSequence();
        seq.setCompanyId(companyId);
        seq.setModulesId(moduleId);
        sequenceId = sequenceRepo.save(seq).getId();

        InventoryUnit unit = new InventoryUnit();
        unit.setCompanyId(companyId);
        unit.setUnitsName("Adet");
        unit.setCreatedBy("test");
        unit.setUpdatedBy("test");
        unitId = unitRepo.save(unit).getId();

        InventoryTransactionType ttype = new InventoryTransactionType();
        ttype.setCompanyId(companyId);
        ttype.setTypeName("Giriş");
        ttype.setCreatedBy("test");
        ttype.setUpdatedBy("test");
        txTypeId = txTypeRepo.save(ttype).getId();

        CurrentCard cc = currentCardService.create(new NewCurrentCard(
                companyId, "CC-LED-" + System.nanoTime(),
                "Ledger Test Müşteri", "x", "y", "VD", "0", new com.turquaz.core.money.Money(BigDecimal.ZERO),
                new com.turquaz.core.money.Money(BigDecimal.ZERO), BigDecimal.ZERO,
                new com.turquaz.core.money.Money(BigDecimal.ZERO), 0, "test"));
        currentCardId = cc.getId();
    }

    @Test
    void stok_kartı_oluşturulur_ve_bulunur() {
        String code = "STK-" + System.nanoTime();
        InventoryCard saved = cardService.create(new NewInventoryCard(
                companyId, code, "Test Ürün", "Tanım", 10, 100, 18, 0, 0, "test"));
        assertThat(cardService.findByCode(companyId, code).getId()).isEqualTo(saved.getId());
    }

    @Test
    void min_maks_tutarsızlığı_reddedilir() {
        assertThatThrownBy(() -> cardService.create(new NewInventoryCard(
                companyId, "BAD-" + System.nanoTime(), "x", "y",
                /*min*/ 50, /*max*/ 10, 18, 0, 0, "test")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Minimum miktar");
    }

    @Test
    void depo_kod_tekildir() {
        String code = "DEP-" + System.nanoTime();
        warehouseService.create(companyId, code, "Ana Depo", "Adres", "İstanbul",
                "0212", "Ana depo açıklaması", "test");
        assertThatThrownBy(() -> warehouseService.create(companyId, code, "X", "y",
                "z", "0", "w", "test"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    @Test
    void stok_bakiyesi_giriş_çıkışlardan_doğru_hesaplanır() {
        InventoryWarehouse warehouse = warehouseService.create(
                companyId, "DEP-LED-" + System.nanoTime(),
                "Ledger Test", "a", "i", "0", "d", "test");
        InventoryCard card = cardService.create(new NewInventoryCard(
                companyId, "STK-LED-" + System.nanoTime(), "Ledger Test", "x",
                0, 1000, 18, 0, 0, "test"));

        Instant t0 = Instant.parse("2026-01-01T10:00:00Z");
        Instant asOf = Instant.parse("2026-12-31T23:59:59Z");
        save(card.getId(), warehouse.getId(), new BigDecimal("100"), BigDecimal.ZERO, t0);
        save(card.getId(), warehouse.getId(), BigDecimal.ZERO, new BigDecimal("30"), t0.plusSeconds(60));
        save(card.getId(), warehouse.getId(), new BigDecimal("50"), BigDecimal.ZERO, t0.plusSeconds(120));

        Quantity onHand = ledger.stockOnHand(companyId, card.getId(), warehouse.getId(), asOf);
        assertThat(onHand).isEqualTo(Quantity.of("120")); // 100 - 30 + 50

        // Tarihsel kesit: yalnız ilk iki işlem dahil → 70 olmalı (100 - 30)
        Quantity midPoint = ledger.stockOnHand(
                companyId, card.getId(), warehouse.getId(), t0.plusSeconds(90));
        assertThat(midPoint).isEqualTo(Quantity.of("70"));

        assertThatThrownBy(() ->
                ledger.ensureSufficient(companyId, card.getId(), warehouse.getId(),
                        Quantity.of("200"), asOf))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Yetersiz stok");

        ledger.ensureSufficient(companyId, card.getId(), warehouse.getId(),
                Quantity.of("50"), asOf);
    }

    private void save(UUID cardId, UUID warehouseId,
                      BigDecimal in, BigDecimal out, Instant at) {
        InventoryTransaction t = new InventoryTransaction();
        t.setCompanyId(companyId);
        t.setInventoryCardsId(cardId);
        t.setInventoryWarehousesId(warehouseId);
        t.setEngineSequencesId(sequenceId);
        t.setInventoryUnitsId(unitId);
        t.setExchangeRateId(exchangeRateId);
        t.setCurrentCardsId(currentCardId);
        t.setTransactionType(txTypeId);
        t.setAmountIn(in);
        t.setAmountOut(out);
        t.setUnitPrice(BigDecimal.ZERO);
        t.setTotalPrice(BigDecimal.ZERO);
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
        txRepo.save(t);
    }
}
