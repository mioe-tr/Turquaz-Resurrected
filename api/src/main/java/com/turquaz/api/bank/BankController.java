/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.bank;

import com.turquaz.api.bank.BankDtos.BankCardResponse;
import com.turquaz.api.bank.BankDtos.BankMovementRequest;
import com.turquaz.api.bank.BankDtos.BankMovementResponse;
import com.turquaz.api.bank.BankDtos.CreateBankCardRequest;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.persistence.accounting.PostingContext;
import com.turquaz.persistence.bank.BankCardService;
import com.turquaz.persistence.bank.BankMovement;
import com.turquaz.persistence.bank.BankTransactionService;
import com.turquaz.persistence.bank.NewBankCard;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private final BankCardService cardService;
    private final BankTransactionService txService;

    public BankController(BankCardService cardService, BankTransactionService txService) {
        this.cardService = cardService;
        this.txService = txService;
    }

    @GetMapping("/cards")
    public List<BankCardResponse> listCards() {
        return cardService.listAll(CurrentUser.companyId()).stream()
                .map(BankCardResponse::from).toList();
    }

    @GetMapping("/cards/by-code/{code}")
    public BankCardResponse cardByCode(@PathVariable String code) {
        return BankCardResponse.from(cardService.findByCode(CurrentUser.companyId(), code));
    }

    @PostMapping("/cards")
    public ResponseEntity<BankCardResponse> createCard(@Valid @RequestBody CreateBankCardRequest req) {
        var saved = cardService.create(new NewBankCard(
                CurrentUser.companyId(),
                req.code(), req.bankName(), req.branchName(),
                req.accountNo(), req.definition(), req.currencyId(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(BankCardResponse.from(saved));
    }

    @PostMapping("/transactions/deposit")
    public ResponseEntity<BankMovementResponse> deposit(@Valid @RequestBody BankMovementRequest req) {
        var tx = txService.recordDeposit(toMovement(req));
        return ResponseEntity.status(201).body(
                BankMovementResponse.from(tx, req.amountMoney(), "DEPOSIT"));
    }

    @PostMapping("/transactions/withdrawal")
    public ResponseEntity<BankMovementResponse> withdrawal(@Valid @RequestBody BankMovementRequest req) {
        var tx = txService.recordWithdrawal(toMovement(req));
        return ResponseEntity.status(201).body(
                BankMovementResponse.from(tx, req.amountMoney(), "WITHDRAWAL"));
    }

    private BankMovement toMovement(BankMovementRequest req) {
        PostingContext ctx = new PostingContext(
                CurrentUser.companyId(),
                req.journalId(), req.transactionTypeId(),
                req.moduleId(), req.engineSequenceId(),
                req.exchangeRateId(), req.exchangeRate(),
                CurrentUser.username());
        return new BankMovement(
                req.bankCardId(), req.bankAccountingAccountId(),
                req.counterAccountId(), req.bankTransactionsBillsId(),
                req.amountMoney(), req.date(), req.documentNo(), req.description(), ctx);
    }
}
