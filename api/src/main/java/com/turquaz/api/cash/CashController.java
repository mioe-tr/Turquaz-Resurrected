/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.cash;

import com.turquaz.api.cash.CashDtos.CashCardResponse;
import com.turquaz.api.cash.CashDtos.CashMovementRequest;
import com.turquaz.api.cash.CashDtos.CashMovementResponse;
import com.turquaz.api.cash.CashDtos.CreateCashCardRequest;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.cash.CashCardService;
import com.turquaz.persistence.cash.CashTransactionService;
import com.turquaz.persistence.cash.CashTransactionService.CashMovement;
import com.turquaz.persistence.cash.CashTransactionService.CashRow;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cash")
public class CashController {

    private final CashCardService cardService;
    private final CashTransactionService txService;

    public CashController(CashCardService cardService, CashTransactionService txService) {
        this.cardService = cardService;
        this.txService = txService;
    }

    @GetMapping("/cards")
    public List<CashCardResponse> listCards() {
        return cardService.listAll(CurrentUser.companyId()).stream()
                .map(CashCardResponse::from).toList();
    }

    @PostMapping("/cards")
    public ResponseEntity<CashCardResponse> createCard(@Valid @RequestBody CreateCashCardRequest req) {
        var saved = cardService.create(
                CurrentUser.companyId(),
                req.name(), req.definition(),
                req.accountingAccountsId(),
                CurrentUser.username());
        return ResponseEntity.status(201).body(CashCardResponse.from(saved));
    }

    @PostMapping("/transactions")
    public ResponseEntity<CashMovementResponse> recordTransaction(
            @Valid @RequestBody CashMovementRequest req) {
        PostingContext ctx = new PostingContext(
                CurrentUser.companyId(),
                req.journalId(), req.transactionTypeId(),
                req.moduleId(), req.engineSequenceId(),
                req.exchangeRateId(), req.exchangeRate(),
                CurrentUser.username());
        List<CashRow> rows = req.rows().stream()
                .map(r -> new CashRow(r.counterAccountId(), r.debitMoney(), r.creditMoney(), r.description()))
                .toList();
        CashMovement movement = new CashMovement(
                req.cashCardId(), req.cashTransactionTypeId(),
                req.date(), req.documentNo(), req.description(),
                rows, ctx);
        var tx = txService.record(movement);
        return ResponseEntity.status(201).body(CashMovementResponse.from(tx));
    }
}
