/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.current;

import com.turquaz.api.current.CurrentCardDtos.CreateCurrentCardRequest;
import com.turquaz.api.current.CurrentCardDtos.CurrentCardResponse;
import com.turquaz.api.current.CurrentCardDtos.UpdateLimitsRequest;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.current.CurrentCard;
import com.turquaz.persistence.current.CurrentCardService;
import com.turquaz.persistence.current.NewCurrentCard;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/current-cards")
public class CurrentCardController {

    private final CurrentCardService service;

    public CurrentCardController(CurrentCardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CurrentCardResponse>> list() {
        List<CurrentCardResponse> body = service.listAll(CurrentUser.companyId()).stream()
                .map(CurrentCardResponse::from).toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<CurrentCardResponse> byCode(@PathVariable String code) {
        return ResponseEntity.ok(CurrentCardResponse.from(
                service.findByCode(CurrentUser.companyId(), code)));
    }

    @PostMapping
    public ResponseEntity<CurrentCardResponse> create(@Valid @RequestBody CreateCurrentCardRequest req) {
        CurrentCard saved = service.create(new NewCurrentCard(
                CurrentUser.companyId(),
                req.code(), req.name(), req.definition(), req.address(),
                req.taxDepartment(), req.taxNumber(),
                req.creditLimitMoney(), req.riskLimitMoney(),
                req.discountRate(), req.discountPaymentMoney(),
                req.daysToValue(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(CurrentCardResponse.from(saved));
    }

    @PutMapping("/{id}/limits")
    public ResponseEntity<CurrentCardResponse> updateLimits(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody UpdateLimitsRequest req) {
        CurrentCard updated = service.updateLimits(
                id, Money.of(req.creditLimit()), Money.of(req.riskLimit()),
                CurrentUser.username());
        return ResponseEntity.ok(CurrentCardResponse.from(updated));
    }
}
