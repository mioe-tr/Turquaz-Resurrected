/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class AccountingApi {

    private final RestClient client;

    public AccountingApi(RestClient client) { this.client = client; }

    public JournalEntryResponse postJournal(PostJournalRequest req) {
        return client.post("/api/v1/accounting/journal", req, JournalEntryResponse.class);
    }

    public List<TrialBalanceLine> trialBalance(Instant asOf) {
        String url = "/api/v1/accounting/trial-balance"
                + (asOf == null ? "" : "?asOf=" + asOf);
        return client.get(url, new TypeReference<List<TrialBalanceLine>>() {});
    }

    public AccountBalance accountBalance(String accountId, Instant asOf) {
        String url = "/api/v1/accounting/accounts/" + accountId + "/balance"
                + (asOf == null ? "" : "?asOf=" + asOf);
        return client.get(url, AccountBalance.class);
    }

    public static class JournalLineRequest {
        public String accountId;
        public BigDecimal debit;
        public BigDecimal credit;
        public String description;
    }

    public static class PostJournalRequest {
        public LocalDate transactionDate;
        public String documentNo;
        public String description;
        public String journalId;
        public String transactionTypeId;
        public String moduleId;
        public String engineSequenceId;
        public String exchangeRateId;
        public BigDecimal exchangeRate;
        public List<JournalLineRequest> lines;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JournalEntryResponse {
        public String transactionId;
        public String journalId;
        public LocalDate transactionDate;
        public String documentNo;
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrialBalanceLine {
        public String accountId;
        public BigDecimal totalDebit;
        public BigDecimal totalCredit;
        public BigDecimal net;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountBalance {
        public String accountId;
        public BigDecimal totalDebit;
        public BigDecimal totalCredit;
        public BigDecimal net;
        public Instant asOf;
    }
}
