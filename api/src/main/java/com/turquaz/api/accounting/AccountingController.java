/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.accounting;

import com.turquaz.api.accounting.AccountingDtos.AccountBalanceResponse;
import com.turquaz.api.accounting.AccountingDtos.JournalEntryResponse;
import com.turquaz.api.accounting.AccountingDtos.JournalLineRequest;
import com.turquaz.api.accounting.AccountingDtos.PostJournalRequest;
import com.turquaz.api.accounting.AccountingDtos.TrialBalanceLineResponse;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.accounting.JournalEntry;
import com.turquaz.core.accounting.JournalLine;
import com.turquaz.persistence.accounting.JournalService;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.accounting.TrialBalanceService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounting")
public class AccountingController {

    private final JournalService journalService;
    private final TrialBalanceService trialBalanceService;

    public AccountingController(
            JournalService journalService,
            TrialBalanceService trialBalanceService) {
        this.journalService = journalService;
        this.trialBalanceService = trialBalanceService;
    }

    @PostMapping("/journal")
    public ResponseEntity<JournalEntryResponse> postJournal(
            @Valid @RequestBody PostJournalRequest req) {
        List<JournalLine> lines = req.lines().stream()
                .map(l -> new JournalLine(
                        l.accountId(), l.debitMoney(), l.creditMoney(), l.description()))
                .toList();
        JournalEntry entry = new JournalEntry(
                req.transactionDate(), req.documentNo(), req.description(), lines);

        PostingContext ctx = new PostingContext(
                CurrentUser.companyId(),
                req.journalId(), req.transactionTypeId(),
                req.moduleId(), req.engineSequenceId(),
                req.exchangeRateId(), req.exchangeRate(),
                CurrentUser.username());

        var tx = journalService.post(entry, ctx);
        return ResponseEntity.status(201).body(JournalEntryResponse.from(tx));
    }

    @GetMapping("/trial-balance")
    public List<TrialBalanceLineResponse> trialBalance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant asOf) {
        Instant effective = (asOf != null) ? asOf : Instant.now();
        return trialBalanceService.trialBalance(CurrentUser.companyId(), effective).stream()
                .map(TrialBalanceLineResponse::from).toList();
    }

    @GetMapping("/accounts/{accountId}/balance")
    public AccountBalanceResponse accountBalance(
            @PathVariable UUID accountId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant asOf) {
        Instant effective = (asOf != null) ? asOf : Instant.now();
        return AccountBalanceResponse.from(
                trialBalanceService.accountBalance(
                        CurrentUser.companyId(), accountId, effective),
                effective);
    }
}
