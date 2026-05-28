/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.bank.BankCardService;
import com.turquaz.persistence.bank.BanksCard;
import com.turquaz.persistence.bank.NewBankCard;
import com.turquaz.persistence.bill.Bill;
import com.turquaz.persistence.bill.BillService;
import com.turquaz.persistence.bill.NewBill;
import com.turquaz.persistence.bill.NewOrder;
import com.turquaz.persistence.bill.Order;
import com.turquaz.persistence.bill.OrderService;
import com.turquaz.persistence.cheque.ChequeCheque;
import com.turquaz.persistence.cheque.ChequeChequesRoll;
import com.turquaz.persistence.cheque.ChequeRoll;
import com.turquaz.persistence.cheque.ChequeRollService;
import com.turquaz.persistence.cheque.ChequeService;
import com.turquaz.persistence.cheque.ChequeTransactionType;
import com.turquaz.persistence.cheque.ChequeTransactionTypeRepository;
import com.turquaz.persistence.cheque.NewCheque;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.common.Currency;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import com.turquaz.persistence.consignment.Consignment;
import com.turquaz.persistence.consignment.ConsignmentService;
import com.turquaz.persistence.consignment.NewConsignment;
import com.turquaz.persistence.current.CurrentCard;
import com.turquaz.persistence.current.CurrentCardService;
import com.turquaz.persistence.current.NewCurrentCard;
import com.turquaz.persistence.engine.EngineSequence;
import com.turquaz.persistence.engine.EngineSequenceRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChequeBillOrderConsignmentTest {

    @Autowired CompanyRepository companies;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired ModuleRepository moduleRepo;

    @Autowired ChequeService chequeService;
    @Autowired ChequeRollService rollService;
    @Autowired ChequeTransactionTypeRepository chequeTypeRepo;

    @Autowired BillService billService;
    @Autowired OrderService orderService;
    @Autowired ConsignmentService consignmentService;
    @Autowired BankCardService bankCardService;
    @Autowired CurrentCardService currentCardService;

    private UUID companyId;
    private UUID currencyId;
    private UUID exchangeRateId;
    private UUID sequenceId;
    private UUID bankCardId;
    private UUID currentCardId;
    private UUID chequeTxTypeId;

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
        currencyId = currency.getId();

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

        BanksCard bank = bankCardService.create(new NewBankCard(
                companyId, "BANK-" + System.nanoTime(),
                "Ziraat", "Merkez", "1", "x", currencyId, "test"));
        bankCardId = bank.getId();

        CurrentCard cc = currentCardService.create(new NewCurrentCard(
                companyId, "CC-" + System.nanoTime(),
                "Test Müşteri", "x", "y", "VD", "0",
                Money.ZERO, Money.ZERO, BigDecimal.ZERO, Money.ZERO, 0, "test"));
        currentCardId = cc.getId();

        // Seed'lenmiş çek işlem türlerinden birini kullan (9 satır yüklü)
        chequeTxTypeId = chequeTypeRepo.findAll().getFirst().getId();
    }

    // -------------------- Çek/Senet --------------------

    @Test
    void çek_oluşturulur_ve_portföye_atanır() {
        ChequeCheque alinan = chequeService.create(buildCheque("CK-1", ChequeService.TYPE_RECEIVED));
        assertThat(chequeService.listReceived(companyId)).extracting(ChequeCheque::getId).contains(alinan.getId());
        assertThat(chequeService.listGiven(companyId)).extracting(ChequeCheque::getId).doesNotContain(alinan.getId());

        ChequeRoll roll = rollService.create(
                companyId, "ROLL-" + System.nanoTime(), LocalDate.now(),
                chequeTxTypeId, currentCardId, bankCardId, sequenceId, true, "test");

        ChequeChequesRoll link = rollService.assignChequeToRoll(
                companyId, alinan.getId(), roll.getId(), "test");
        assertThat(link.getChequeChequesId()).isEqualTo(alinan.getId());
        assertThat(link.getChequeRollsId()).isEqualTo(roll.getId());
    }

    @Test
    void aynı_banka_ve_çek_no_iki_kez_oluşturulamaz() {
        String fixedNo = "DUP-CK-" + System.nanoTime();
        chequeService.create(buildChequeFixedNo(fixedNo, ChequeService.TYPE_RECEIVED));
        assertThatThrownBy(() -> chequeService.create(buildChequeFixedNo(fixedNo, ChequeService.TYPE_RECEIVED)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    private NewCheque buildChequeFixedNo(String no, int type) {
        return new NewCheque(
                companyId, no, "PRT-1",
                bankCardId, currencyId, exchangeRateId, BigDecimal.ONE,
                "Garanti", "Levent", "1234567",
                Money.of("5000"), "Borçlu A.Ş.", "İstanbul",
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 5, 28),
                type, "test");
    }

    @Test
    void vade_keside_den_önceyse_red() {
        assertThatThrownBy(() -> chequeService.create(new NewCheque(
                companyId, "CK-BAD-" + System.nanoTime(), "1", bankCardId, currencyId,
                exchangeRateId, BigDecimal.ONE, "Garanti", "Levent", "1234",
                Money.of("1000"), "Borçlu A.Ş.", "İstanbul",
                /*due*/ LocalDate.of(2026, 1, 1),
                /*value*/ LocalDate.of(2026, 2, 1),
                ChequeService.TYPE_RECEIVED, "test")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Vade tarihi");
    }

    @Test
    void geçersiz_çek_türü_red() {
        assertThatThrownBy(() -> chequeService.create(new NewCheque(
                companyId, "X" + System.nanoTime(), "1", bankCardId, currencyId,
                exchangeRateId, BigDecimal.ONE, "Garanti", "Levent", "1234",
                Money.of("100"), "x", "y",
                LocalDate.now().plusDays(30), LocalDate.now(),
                /*type*/ 99, "test")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Geçersiz çek türü");
    }

    // -------------------- Fatura --------------------

    @Test
    void fatura_oluşturulur_yazdırılır_kapatılır() {
        Bill bill = billService.create(new NewBill(
                companyId, BillService.TYPE_SALES,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10),
                "FT-" + System.nanoTime(), "Test satış faturası",
                currentCardId, exchangeRateId, sequenceId, "test"));
        assertThat(bill.getBillsPrinted()).isFalse();
        assertThat(bill.getIsOpen()).isTrue();

        Bill printed = billService.markPrinted(bill.getId(), "admin");
        assertThat(printed.getBillsPrinted()).isTrue();

        Bill closed = billService.close(bill.getId(), "admin");
        assertThat(closed.getIsOpen()).isFalse();

        assertThatThrownBy(() -> billService.close(bill.getId(), "admin"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kapalı");
    }

    @Test
    void fatura_belge_no_tekildir() {
        String docNo = "FT-DUP-" + System.nanoTime();
        billService.create(buildBill(docNo));
        assertThatThrownBy(() -> billService.create(buildBill(docNo)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    @Test
    void fatura_vade_tarihi_red() {
        assertThatThrownBy(() -> billService.create(new NewBill(
                companyId, BillService.TYPE_SALES,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1),
                "FT-BAD-" + System.nanoTime(), "ters tarih",
                currentCardId, exchangeRateId, sequenceId, "test")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Vade tarihi");
    }

    // -------------------- Sipariş --------------------

    @Test
    void sipariş_oluşturulur_ve_teslim_edilir() {
        Bill bill = billService.create(buildBill("FT-ORD-" + System.nanoTime()));
        Order order = orderService.create(new NewOrder(
                companyId, OrderService.TYPE_SALES,
                /*docNo*/ (int)(System.nanoTime() % 100000),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 3, 15),
                currentCardId, bill.getId(),
                "Test sipariş",
                /*discount%*/ 10, /*vat%*/ 18,
                Money.of("100"), Money.of("50"),
                Money.of("180"), Money.of("1000"), "test"));
        assertThat(order.getOrdersDelivered()).isEqualTo(0);

        Order delivered = orderService.markDelivered(order.getId(), "admin");
        assertThat(delivered.getOrdersDelivered()).isEqualTo(1);

        assertThatThrownBy(() -> orderService.markDelivered(order.getId(), "admin"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten teslim");
    }

    @Test
    void sipariş_teslim_tarihi_sipariş_tarihinden_önce_olamaz() {
        Bill bill = billService.create(buildBill("FT-ORDBAD-" + System.nanoTime()));
        assertThatThrownBy(() -> orderService.create(new NewOrder(
                companyId, OrderService.TYPE_SALES,
                1, LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 7, 1),
                /*deliver*/ LocalDate.of(2026, 5, 1),
                currentCardId, bill.getId(), "x", 0, 0,
                Money.ZERO, Money.ZERO, Money.ZERO, Money.of("100"), "test")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Teslim tarihi");
    }

    // -------------------- Konsinye --------------------

    @Test
    void konsinye_oluşturulur_ve_yazdırılır() {
        Consignment c = consignmentService.create(new NewConsignment(
                companyId, ConsignmentService.TYPE_OUT,
                LocalDate.now(),
                "KNS-" + System.nanoTime(), "FT-REF-001",
                "Verilen konsinye", currentCardId, exchangeRateId, sequenceId, "test"));
        assertThat(c.getConsignmentsPrinted()).isFalse();

        Consignment printed = consignmentService.markPrinted(c.getId(), "admin");
        assertThat(printed.getConsignmentsPrinted()).isTrue();
    }

    @Test
    void konsinye_belge_no_tekildir() {
        String no = "KNS-DUP-" + System.nanoTime();
        consignmentService.create(buildConsignment(no));
        assertThatThrownBy(() -> consignmentService.create(buildConsignment(no)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    // -------------------- yardımcılar --------------------

    private NewCheque buildCheque(String no, int type) {
        return new NewCheque(
                companyId, no + "-" + System.nanoTime(), "PRT-1",
                bankCardId, currencyId, exchangeRateId, BigDecimal.ONE,
                "Garanti", "Levent", "1234567",
                Money.of("5000"), "Borçlu A.Ş.", "İstanbul",
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 5, 28),
                type, "test");
    }

    private NewBill buildBill(String docNo) {
        return new NewBill(
                companyId, BillService.TYPE_SALES,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1),
                docNo, "Test", currentCardId, exchangeRateId, sequenceId, "test");
    }

    private NewConsignment buildConsignment(String no) {
        return new NewConsignment(
                companyId, ConsignmentService.TYPE_OUT, LocalDate.now(),
                no, "REF-001", "Test", currentCardId, exchangeRateId, sequenceId, "test");
    }
}
