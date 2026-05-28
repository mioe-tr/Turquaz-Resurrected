/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.bank.BankDtos.BankCardResponse;
import com.turquaz.api.bank.BankDtos.BankMovementRequest;
import com.turquaz.api.bank.BankDtos.CreateBankCardRequest;
import com.turquaz.api.cash.CashDtos.CashCardResponse;
import com.turquaz.api.cash.CashDtos.CashMovementRequest;
import com.turquaz.api.cash.CashDtos.CashRowRequest;
import com.turquaz.api.cash.CashDtos.CreateCashCardRequest;
import com.turquaz.api.cheque.ChequeDtos.AssignChequeRequest;
import com.turquaz.api.cheque.ChequeDtos.ChequeResponse;
import com.turquaz.api.cheque.ChequeDtos.ChequeRollResponse;
import com.turquaz.api.cheque.ChequeDtos.CreateChequeRequest;
import com.turquaz.api.cheque.ChequeDtos.CreateChequeRollRequest;
import com.turquaz.api.support.AccountingEnvironment;
import com.turquaz.api.support.AuthenticatedTest;
import com.turquaz.persistence.bank.BanksTransactionBill;
import com.turquaz.persistence.bank.BanksTransactionBillRepository;
import com.turquaz.persistence.bank.BanksTransactionType;
import com.turquaz.persistence.bank.BanksTransactionTypeRepository;
import com.turquaz.persistence.cash.CashTransactionType;
import com.turquaz.persistence.cash.CashTransactionTypeRepository;
import com.turquaz.persistence.cheque.ChequeTransactionTypeRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class BankCashChequeControllerTest extends AuthenticatedTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired AccountingEnvironment env;
    @Autowired BanksTransactionTypeRepository banksTxTypeRepo;
    @Autowired BanksTransactionBillRepository banksBillRepo;
    @Autowired CashTransactionTypeRepository cashTxTypeRepo;
    @Autowired ChequeTransactionTypeRepository chequeTxTypeRepo;

    @BeforeEach
    void setUp() {
        initAuth("bcc-ctrl");
        env.ensureFor(companyId);
    }

    // -------------------- Banka --------------------

    @Test
    void banka_kartı_oluştur_ve_giriş_yap() throws Exception {
        CreateBankCardRequest reqCard = new CreateBankCardRequest(
                "BNK-" + System.nanoTime(), "Ziraat", "Merkez", "1234",
                "Ana hesap", env.currencyId);
        MvcResult card = mvc.perform(MockMvcRequestBuilders.post("/api/v1/bank/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reqCard)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        var cardBody = json.readValue(
                card.getResponse().getContentAsString(), BankCardResponse.class);

        // Banka fiş başlığı + tip kur
        BanksTransactionType bt = new BanksTransactionType();
        bt.setCompanyId(companyId);
        bt.setTransactionTypeName("Genel");
        bt.setCreatedBy("test");
        bt.setUpdatedBy("test");
        banksTxTypeRepo.save(bt);
        BanksTransactionBill bill = new BanksTransactionBill();
        bill.setCompanyId(companyId);
        bill.setBanksTransactionTypesId(bt.getId());
        bill.setEngineSequencesId(env.sequenceId);
        bill.setTransactionBillDate(Instant.now());
        bill.setTransactionBillDefinition("test");
        bill.setTransactionBillNo("BB-" + System.nanoTime());
        bill.setCreatedBy("test");
        bill.setUpdatedBy("test");
        banksBillRepo.save(bill);

        BankMovementRequest dep = new BankMovementRequest(
                cardBody.id(), env.accountA, env.accountB, bill.getId(),
                new BigDecimal("5000"), LocalDate.now(),
                "DEP-API", "Tahsilat",
                env.journalId, env.txTypeId, env.moduleId, env.sequenceId,
                env.exchangeRateId, env.exchangeRate);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bank/transactions/deposit")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(dep)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.direction").value("DEPOSIT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(5000.0));
    }

    @Test
    void banka_negatif_tutar_400() throws Exception {
        // Bean validation @Positive zaten 400 fırlatır (servise gitmeden)
        String body = """
                {"bankCardId":"%s","bankAccountingAccountId":"%s","counterAccountId":"%s",
                 "bankTransactionsBillsId":"%s","amount":-1,"date":"2026-01-01",
                 "documentNo":"X","description":"X",
                 "journalId":"%s","transactionTypeId":"%s","moduleId":"%s",
                 "engineSequenceId":"%s","exchangeRateId":"%s","exchangeRate":1.0}
                """.formatted(UUID.randomUUID(), env.accountA, env.accountB,
                UUID.randomUUID(), env.journalId, env.txTypeId,
                env.moduleId, env.sequenceId, env.exchangeRateId);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bank/transactions/deposit")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // -------------------- Kasa --------------------

    @Test
    void kasa_kartı_oluştur_ve_hareket_postla() throws Exception {
        CreateCashCardRequest reqCard = new CreateCashCardRequest(
                "Ana Kasa", "Merkez kasa", env.accountA);
        MvcResult card = mvc.perform(MockMvcRequestBuilders.post("/api/v1/cash/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reqCard)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        var cardBody = json.readValue(
                card.getResponse().getContentAsString(), CashCardResponse.class);

        CashTransactionType ct = new CashTransactionType();
        ct.setCompanyId(companyId);
        ct.setCashTransationTypeName("Tahsilat");
        ct.setCreatedBy("test");
        ct.setUpdatedBy("test");
        cashTxTypeRepo.save(ct);

        CashMovementRequest mov = new CashMovementRequest(
                cardBody.id(), ct.getId(), LocalDate.now(),
                "CASH-API", "Test",
                List.of(
                        new CashRowRequest(env.accountB, new BigDecimal("300"), BigDecimal.ZERO, "A müşteri"),
                        new CashRowRequest(env.accountC, new BigDecimal("200"), BigDecimal.ZERO, "B müşteri")),
                env.journalId, env.txTypeId, env.moduleId, env.sequenceId,
                env.exchangeRateId, env.exchangeRate);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/cash/transactions")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(mov)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.documentNo").value("CASH-API"));
    }

    // -------------------- Çek --------------------

    @Test
    void çek_oluştur_ve_portföye_ata() throws Exception {
        // Banka kartı + cari kart gerekli
        CreateBankCardRequest reqBank = new CreateBankCardRequest(
                "B-CK-" + System.nanoTime(), "İş", "M", "1", "x", env.currencyId);
        MvcResult bankRes = mvc.perform(MockMvcRequestBuilders.post("/api/v1/bank/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reqBank)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        var bank = json.readValue(bankRes.getResponse().getContentAsString(), BankCardResponse.class);

        // Cari kart oluştur (REST API ile)
        String currentBody = """
                {"code":"CC-%d","name":"x","definition":"y","address":"z",
                 "taxDepartment":"VD","taxNumber":"0",
                 "creditLimit":0,"riskLimit":0,"discountRate":0,
                 "discountPayment":0,"daysToValue":0}
                """.formatted(System.nanoTime());
        MvcResult curRes = mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(currentBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        UUID currentId = UUID.fromString(
                json.readTree(curRes.getResponse().getContentAsString()).get("id").asText());

        // Çek tip seed'den (turq_cheque_transaction_types 9 satır)
        UUID chequeTxType = chequeTxTypeRepo.findAll().getFirst().getId();

        // Çek oluştur
        CreateChequeRequest cheque = new CreateChequeRequest(
                "CK-API-" + System.nanoTime(), "PRT-1",
                bank.id(), env.currencyId, env.exchangeRateId, env.exchangeRate,
                "Garanti", "Levent", "12345",
                new BigDecimal("5000"), "Borçlu A.Ş.", "İstanbul",
                LocalDate.of(2026, 12, 31), LocalDate.of(2026, 5, 28),
                /*type*/ 1);
        MvcResult ckRes = mvc.perform(MockMvcRequestBuilders.post("/api/v1/cheques")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(cheque)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        var ckBody = json.readValue(ckRes.getResponse().getContentAsString(), ChequeResponse.class);

        // Çek portföyü oluştur
        CreateChequeRollRequest roll = new CreateChequeRollRequest(
                "ROLL-API-" + System.nanoTime(), LocalDate.now(),
                chequeTxType, currentId, bank.id(), env.sequenceId, true);
        MvcResult rollRes = mvc.perform(MockMvcRequestBuilders.post("/api/v1/cheque-rolls")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(roll)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        var rollBody = json.readValue(rollRes.getResponse().getContentAsString(), ChequeRollResponse.class);

        // Çeki portföye ata
        AssignChequeRequest assign = new AssignChequeRequest(ckBody.id(), rollBody.id());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/cheque-rolls/assign")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(assign)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.chequeId").value(ckBody.id().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rollId").value(rollBody.id().toString()));
    }

    @Test
    void çek_geçersiz_tür_400() throws Exception {
        CreateChequeRequest req = new CreateChequeRequest(
                "X-" + System.nanoTime(), "1",
                UUID.randomUUID(), env.currencyId, env.exchangeRateId, env.exchangeRate,
                "x", "y", "z",
                new BigDecimal("100"), "borçlu", "y",
                LocalDate.now().plusDays(10), LocalDate.now(),
                /*type*/ 99);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/cheques")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
