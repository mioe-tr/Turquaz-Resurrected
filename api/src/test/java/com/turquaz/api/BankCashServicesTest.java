/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.accounting.AccountingException;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingAccountRepository;
import com.turquaz.persistence.accounting.AccountingJournal;
import com.turquaz.persistence.accounting.AccountingJournalRepository;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.accounting.AccountingTransactionColumnRepository;
import com.turquaz.persistence.accounting.AccountingTransactionRepository;
import com.turquaz.persistence.accounting.AccountingTransactionType;
import com.turquaz.persistence.accounting.AccountingTransactionTypeRepository;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.bank.BankCardService;
import com.turquaz.persistence.bank.BankMovement;
import com.turquaz.persistence.bank.BankTransactionService;
import com.turquaz.persistence.bank.BanksCard;
import com.turquaz.persistence.bank.BanksTransactionBill;
import com.turquaz.persistence.bank.BanksTransactionBillRepository;
import com.turquaz.persistence.bank.BanksTransactionRepository;
import com.turquaz.persistence.bank.BanksTransactionType;
import com.turquaz.persistence.bank.BanksTransactionTypeRepository;
import com.turquaz.persistence.bank.NewBankCard;
import com.turquaz.persistence.cash.CashCard;
import com.turquaz.persistence.cash.CashCardService;
import com.turquaz.persistence.cash.CashTransactionRepository;
import com.turquaz.persistence.cash.CashTransactionRowRepository;
import com.turquaz.persistence.cash.CashTransactionService;
import com.turquaz.persistence.cash.CashTransactionService.CashMovement;
import com.turquaz.persistence.cash.CashTransactionService.CashRow;
import com.turquaz.persistence.cash.CashTransactionType;
import com.turquaz.persistence.cash.CashTransactionTypeRepository;
import com.turquaz.persistence.common.CompanyRepository;
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
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankCashServicesTest {

    @Autowired CompanyRepository companies;
    @Autowired ModuleRepository moduleRepo;
    @Autowired CurrencyRepository currencyRepo;
    @Autowired CurrencyExchangeRateRepository rateRepo;
    @Autowired EngineSequenceRepository sequenceRepo;
    @Autowired AccountingJournalRepository journalRepo;
    @Autowired AccountingTransactionTypeRepository txTypeRepo;
    @Autowired AccountingAccountRepository accountRepo;
    @Autowired AccountingTransactionRepository accTxRepo;
    @Autowired AccountingTransactionColumnRepository accColRepo;

    @Autowired BankCardService bankCardService;
    @Autowired BankTransactionService bankTxService;
    @Autowired BanksTransactionRepository banksTxRepo;
    @Autowired BanksTransactionBillRepository banksBillRepo;
    @Autowired BanksTransactionTypeRepository banksTxTypeRepo;

    @Autowired CashCardService cashCardService;
    @Autowired CashTransactionService cashTxService;
    @Autowired CashTransactionRepository cashTxRepo;
    @Autowired CashTransactionRowRepository cashRowRepo;
    @Autowired CashTransactionTypeRepository cashTxTypeRepo;

    private PostingContext ctx;
    private UUID companyId;
    private UUID bankAccountingAccountId;
    private UUID cashAccountingAccountId;
    private UUID counterAccountId; // örn. cari hesap

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
        UUID rateId = rateRepo.save(rate).getId();

        EngineSequence seq = new EngineSequence();
        seq.setCompanyId(companyId);
        seq.setModulesId(moduleId);
        UUID sequenceId = sequenceRepo.save(seq).getId();

        AccountingJournal journal = new AccountingJournal();
        journal.setCompanyId(companyId);
        journal.setJournalDate(Instant.now());
        journal.setCreatedBy("test");
        journal.setUpdatedBy("test");
        UUID journalId = journalRepo.save(journal).getId();

        AccountingTransactionType txType = new AccountingTransactionType();
        txType.setCompanyId(companyId);
        txType.setTypesName("Test");
        txType.setCreatedBy("test");
        txType.setUpdatedBy("test");
        UUID txTypeId = txTypeRepo.save(txType).getId();

        // 461 seed hesabından 3 farklı id seç
        var accounts = accountRepo.findAll();
        bankAccountingAccountId = accounts.get(0).getId();
        cashAccountingAccountId = accounts.get(1).getId();
        counterAccountId = accounts.get(2).getId();

        ctx = new PostingContext(
                companyId, journalId, txTypeId, moduleId, sequenceId,
                rateId, new BigDecimal("1.0"), "test");
    }

    @Test
    void banka_kartı_unique_code() {
        String code = "BANK-" + System.nanoTime();
        BanksCard saved = bankCardService.create(new NewBankCard(
                companyId, code, "Garanti", "Levent", "1234-5678",
                "Şirket ana hesabı", currencyId(), "test"));
        assertThat(bankCardService.findByCode(companyId, code).getId()).isEqualTo(saved.getId());
        assertThatThrownBy(() -> bankCardService.create(new NewBankCard(
                companyId, code, "x", "y", "z", "w", currencyId(), "test")))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void banka_para_girişi_dengeli_yevmiye_üretir() {
        BanksCard card = bankCardService.create(new NewBankCard(
                companyId, "B-IN-" + System.nanoTime(), "Iş", "Maslak", "1",
                "x", currencyId(), "test"));
        BanksTransactionBill bill = saveBanksBill();

        long accColsBefore = accColRepo.count();
        AccountingTransaction posted = bankTxService.recordDeposit(new BankMovement(
                card.getId(), bankAccountingAccountId, counterAccountId, bill.getId(),
                Money.of("5000"), LocalDate.now(), "DEP-1", "Müşteriden tahsilat", ctx));

        assertThat(accTxRepo.findById(posted.getId())).isPresent();
        // 2 yevmiye satırı eklendi
        assertThat(accColRepo.count()).isEqualTo(accColsBefore + 2);

        // Bu karta ait tek BanksTransaction var
        var bankTxs = banksTxRepo.findAll().stream()
                .filter(t -> t.getBanksCardsId().equals(card.getId())).toList();
        assertThat(bankTxs).hasSize(1);
        assertThat(bankTxs.get(0).getDeptAmount()).isEqualByComparingTo("5000");
        assertThat(bankTxs.get(0).getCreditAmount()).isEqualByComparingTo("0");
    }

    @Test
    void banka_para_çıkışı_ters_yönde_postlanır() {
        BanksCard card = bankCardService.create(new NewBankCard(
                companyId, "B-OUT-" + System.nanoTime(), "Iş", "Maslak", "2",
                "x", currencyId(), "test"));
        BanksTransactionBill bill = saveBanksBill();

        bankTxService.recordWithdrawal(new BankMovement(
                card.getId(), bankAccountingAccountId, counterAccountId, bill.getId(),
                Money.of("750"), LocalDate.now(), "WD-1", "Ödeme", ctx));

        var bankTx = banksTxRepo.findAll().stream()
                .filter(t -> t.getBanksCardsId().equals(card.getId())).findFirst().orElseThrow();
        assertThat(bankTx.getDeptAmount()).isEqualByComparingTo("0");
        assertThat(bankTx.getCreditAmount()).isEqualByComparingTo("750");
    }

    @Test
    void sıfır_veya_negatif_tutar_reddedilir() {
        BanksCard card = bankCardService.create(new NewBankCard(
                companyId, "B-NEG-" + System.nanoTime(), "x", "y", "1", "z",
                currencyId(), "test"));
        BanksTransactionBill bill = saveBanksBill();
        assertThatThrownBy(() -> bankTxService.recordDeposit(new BankMovement(
                card.getId(), bankAccountingAccountId, counterAccountId, bill.getId(),
                Money.ZERO, LocalDate.now(), "X", "x", ctx)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("pozitif");
    }

    @Test
    void kasa_hareketi_çoklu_satırla_postlanır() {
        CashCard cashCard = cashCardService.create(
                companyId, "Ana Kasa", "Merkez kasa", cashAccountingAccountId, "test");

        CashTransactionType cashType = new CashTransactionType();
        cashType.setCompanyId(companyId);
        cashType.setCashTransationTypeName("Tahsilat");
        cashType.setCreatedBy("test");
        cashType.setUpdatedBy("test");
        cashTxTypeRepo.save(cashType);

        long cashTxBefore = cashTxRepo.count();
        long cashRowBefore = cashRowRepo.count();
        long accColsBefore = accColRepo.count();

        // İki tahsilat satırı: cari A'dan 300, cari B'den 200 — toplam 500 kasa girişi
        AccountingTransaction posted = cashTxService.record(new CashMovement(
                cashCard.getId(), cashType.getId(), LocalDate.now(), "TAH-1",
                "Günlük tahsilat",
                List.of(
                        new CashRow(counterAccountId, Money.of("300"), Money.ZERO, "A müşterisi"),
                        new CashRow(counterAccountId, Money.of("200"), Money.ZERO, "B müşterisi")),
                ctx));

        // 1 CashTransaction header + 2 row
        assertThat(cashTxRepo.count() - cashTxBefore).isEqualTo(1);
        assertThat(cashRowRepo.count() - cashRowBefore).isEqualTo(2);

        // Yevmiye: 2 karşı debit + 1 kasa credit özet = 3 satır
        assertThat(accColRepo.count()).isEqualTo(accColsBefore + 3);

        // Yevmiye dengeli (post() başarılıysa zaten dengeliydi). Doğrulamak için
        // posted tx'in satırlarını topla.
        var cols = accColRepo.findAll().stream()
                .filter(c -> c.getAccountingTransactionsId().equals(posted.getId())).toList();
        BigDecimal totalDept = cols.stream().map(c -> c.getDeptAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = cols.stream().map(c -> c.getCreditAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalDept).isEqualByComparingTo(totalCredit);
        assertThat(totalDept).isEqualByComparingTo("500");
    }

    @Test
    void kasa_satırı_yoksa_reddedilir() {
        CashCard cashCard = cashCardService.create(
                companyId, "Boş", "x", cashAccountingAccountId, "test");
        CashTransactionType cashType = new CashTransactionType();
        cashType.setCompanyId(companyId);
        cashType.setCashTransationTypeName("X");
        cashType.setCreatedBy("test");
        cashType.setUpdatedBy("test");
        cashTxTypeRepo.save(cashType);
        assertThatThrownBy(() -> cashTxService.record(new CashMovement(
                cashCard.getId(), cashType.getId(), LocalDate.now(), "X", "x",
                List.of(), ctx)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void dengesiz_kasa_hareketi_yevmiyeye_yansımadan_reddedilir() {
        // Bu testin kapsadığı senaryo aslında JournalEntry constructor invariant'ı
        // sayesinde meydana gelemez — kasa satırlarındaki XOR + kontra hesap kuralı
        // her zaman dengeli üretir. Yine de sınır durumu olarak: aynı satırda hem
        // debit hem credit konulamaz (XOR).
        CashCard cashCard = cashCardService.create(
                companyId, "X", "x", cashAccountingAccountId, "test");
        CashTransactionType cashType = new CashTransactionType();
        cashType.setCompanyId(companyId);
        cashType.setCashTransationTypeName("X");
        cashType.setCreatedBy("test");
        cashType.setUpdatedBy("test");
        cashTxTypeRepo.save(cashType);
        assertThatThrownBy(() ->
                new CashRow(counterAccountId, Money.of("100"), Money.of("100"), "iki"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("hem borç hem alacak");
    }

    // --- yardımcılar ---

    private UUID currencyId() {
        return currencyRepo.findAll().get(0).getId();
    }

    private BanksTransactionBill saveBanksBill() {
        BanksTransactionType bt = new BanksTransactionType();
        bt.setCompanyId(companyId);
        bt.setTransactionTypeName("Genel");
        bt.setCreatedBy("test");
        bt.setUpdatedBy("test");
        banksTxTypeRepo.save(bt);

        BanksTransactionBill bill = new BanksTransactionBill();
        bill.setCompanyId(companyId);
        bill.setBanksTransactionTypesId(bt.getId());
        bill.setEngineSequencesId(ctx.engineSequenceId());
        bill.setTransactionBillDate(Instant.now());
        bill.setTransactionBillDefinition("test");
        bill.setTransactionBillNo("BB-" + System.nanoTime());
        bill.setCreatedBy("test");
        bill.setUpdatedBy("test");
        return banksBillRepo.save(bill);
    }
}
