/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Banka + Kasa + Çek API'leri tek dosyada (özet UI ihtiyaçları için). */
public class BankCashChequeApi {

    private final RestClient client;

    public BankCashChequeApi(RestClient client) { this.client = client; }

    // ----- Banka -----

    public List<BankCard> listBankCards() {
        return client.get("/api/v1/bank/cards", new TypeReference<List<BankCard>>() {});
    }

    public BankCard createBankCard(CreateBankCard req) {
        return client.post("/api/v1/bank/cards", req, BankCard.class);
    }

    // ----- Kasa -----

    public List<CashCard> listCashCards() {
        return client.get("/api/v1/cash/cards", new TypeReference<List<CashCard>>() {});
    }

    public CashCard createCashCard(CreateCashCard req) {
        return client.post("/api/v1/cash/cards", req, CashCard.class);
    }

    // ----- Çek -----

    public List<Cheque> listCheques(Integer type) {
        String url = "/api/v1/cheques" + (type == null ? "" : "?type=" + type);
        return client.get(url, new TypeReference<List<Cheque>>() {});
    }

    public Cheque createCheque(CreateCheque req) {
        return client.post("/api/v1/cheques", req, Cheque.class);
    }

    // ----- Records -----

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BankCard {
        public String id;
        public String companyId;
        public String code;
        public String bankName;
        public String branchName;
        public String accountNo;
        public String definition;
        public String currencyId;
    }

    public static class CreateBankCard {
        public String code;
        public String bankName;
        public String branchName;
        public String accountNo;
        public String definition;
        public String currencyId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CashCard {
        public String id;
        public String companyId;
        public String name;
        public String definition;
        public String accountingAccountsId;
    }

    public static class CreateCashCard {
        public String name;
        public String definition;
        public String accountingAccountsId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cheque {
        public String id;
        public String companyId;
        public String chequeNo;
        public String portfolioNo;
        public String bankId;
        public BigDecimal amount;
        public String debtor;
        public LocalDate dueDate;
        public LocalDate valueDate;
        public Integer type;
    }

    public static class CreateCheque {
        public String chequeNo;
        public String portfolioNo;
        public String bankId;
        public String currencyId;
        public String exchangeRateId;
        public BigDecimal exchangeRate;
        public String bankName;
        public String bankBranchName;
        public String bankAccountNo;
        public BigDecimal amount;
        public String debtor;
        public String paymentPlace;
        public LocalDate dueDate;
        public LocalDate valueDate;
        public Integer type;
    }
}
