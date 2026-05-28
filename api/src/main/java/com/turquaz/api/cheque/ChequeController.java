/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.cheque;

import com.turquaz.api.cheque.ChequeDtos.AssignChequeRequest;
import com.turquaz.api.cheque.ChequeDtos.ChequeResponse;
import com.turquaz.api.cheque.ChequeDtos.ChequeRollAssignmentResponse;
import com.turquaz.api.cheque.ChequeDtos.ChequeRollResponse;
import com.turquaz.api.cheque.ChequeDtos.CreateChequeRequest;
import com.turquaz.api.cheque.ChequeDtos.CreateChequeRollRequest;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.cheque.ChequeRollService;
import com.turquaz.persistence.cheque.ChequeService;
import com.turquaz.persistence.cheque.NewCheque;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChequeController {

    private final ChequeService chequeService;
    private final ChequeRollService rollService;

    public ChequeController(ChequeService chequeService, ChequeRollService rollService) {
        this.chequeService = chequeService;
        this.rollService = rollService;
    }

    @GetMapping("/cheques")
    public List<ChequeResponse> listCheques(
            @RequestParam(required = false) Integer type) {
        var list = switch (type == null ? -1 : type) {
            case ChequeService.TYPE_RECEIVED -> chequeService.listReceived(CurrentUser.companyId());
            case ChequeService.TYPE_GIVEN -> chequeService.listGiven(CurrentUser.companyId());
            default -> {
                var received = chequeService.listReceived(CurrentUser.companyId());
                var given = chequeService.listGiven(CurrentUser.companyId());
                var all = new java.util.ArrayList<>(received);
                all.addAll(given);
                yield all;
            }
        };
        return list.stream().map(ChequeResponse::from).toList();
    }

    @PostMapping("/cheques")
    public ResponseEntity<ChequeResponse> createCheque(@Valid @RequestBody CreateChequeRequest req) {
        var saved = chequeService.create(new NewCheque(
                CurrentUser.companyId(),
                req.chequeNo(), req.portfolioNo(),
                req.bankId(), req.currencyId(),
                req.exchangeRateId(), req.exchangeRate(),
                req.bankName(), req.bankBranchName(), req.bankAccountNo(),
                Money.of(req.amount()), req.debtor(), req.paymentPlace(),
                req.dueDate(), req.valueDate(),
                req.type(), CurrentUser.username()));
        return ResponseEntity.status(201).body(ChequeResponse.from(saved));
    }

    @GetMapping("/cheque-rolls")
    public List<ChequeRollResponse> listRolls() {
        return rollService.listAll(CurrentUser.companyId()).stream()
                .map(ChequeRollResponse::from).toList();
    }

    @PostMapping("/cheque-rolls")
    public ResponseEntity<ChequeRollResponse> createRoll(@Valid @RequestBody CreateChequeRollRequest req) {
        var saved = rollService.create(
                CurrentUser.companyId(),
                req.rollNo(), req.date(),
                req.transactionTypeId(), req.currentCardId(), req.bankCardId(),
                req.engineSequenceId(), req.sumChequeAmounts(),
                CurrentUser.username());
        return ResponseEntity.status(201).body(ChequeRollResponse.from(saved));
    }

    @PostMapping("/cheque-rolls/assign")
    public ResponseEntity<ChequeRollAssignmentResponse> assignCheque(
            @Valid @RequestBody AssignChequeRequest req) {
        var link = rollService.assignChequeToRoll(
                CurrentUser.companyId(),
                req.chequeId(), req.rollId(),
                CurrentUser.username());
        return ResponseEntity.status(201).body(ChequeRollAssignmentResponse.from(link));
    }
}
