/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.cheque;

import com.turquaz.persistence.cheque.ChequeCheque;
import com.turquaz.persistence.cheque.ChequeChequesRoll;
import com.turquaz.persistence.cheque.ChequeRoll;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class ChequeDtos {

    private ChequeDtos() {}

    public record ChequeResponse(
            UUID id, UUID companyId, String chequeNo, String portfolioNo,
            UUID bankId, BigDecimal amount, String debtor,
            LocalDate dueDate, LocalDate valueDate, Integer type) {

        public static ChequeResponse from(ChequeCheque c) {
            return new ChequeResponse(
                    c.getId(), c.getCompanyId(),
                    c.getChequesNo(), c.getChequesPortfolioNo(),
                    c.getBanksId(), c.getChequesAmount(), c.getChequesDebtor(),
                    c.getChequesDueDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    c.getChequesValueDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    c.getChequesType());
        }
    }

    public record CreateChequeRequest(
            @NotBlank @Size(max = 50) String chequeNo,
            @NotBlank @Size(max = 30) String portfolioNo,
            @NotNull UUID bankId,
            @NotNull UUID currencyId,
            @NotNull UUID exchangeRateId,
            @NotNull BigDecimal exchangeRate,
            @NotBlank @Size(max = 100) String bankName,
            @NotBlank @Size(max = 100) String bankBranchName,
            @Size(max = 100) String bankAccountNo,
            @NotNull @Positive BigDecimal amount,
            @NotBlank @Size(max = 100) String debtor,
            @Size(max = 50) String paymentPlace,
            @NotNull LocalDate dueDate,
            @NotNull LocalDate valueDate,
            @NotNull @Min(1) @Max(2) Integer type) {
    }

    public record ChequeRollResponse(
            UUID id, UUID companyId, String rollNo,
            UUID currentCardId, UUID bankCardId, Boolean sumChequeAmounts) {

        public static ChequeRollResponse from(ChequeRoll r) {
            return new ChequeRollResponse(
                    r.getId(), r.getCompanyId(),
                    r.getChequeRollNo(),
                    r.getCurrentCardsId(), r.getBanksCardsId(),
                    r.getSumChequeAmounts());
        }
    }

    public record CreateChequeRollRequest(
            @NotBlank @Size(max = 50) String rollNo,
            @NotNull LocalDate date,
            @NotNull UUID transactionTypeId,
            @NotNull UUID currentCardId,
            @NotNull UUID bankCardId,
            @NotNull UUID engineSequenceId,
            @NotNull Boolean sumChequeAmounts) {
    }

    public record AssignChequeRequest(
            @NotNull UUID chequeId,
            @NotNull UUID rollId) {
    }

    public record ChequeRollAssignmentResponse(UUID id, UUID chequeId, UUID rollId) {
        public static ChequeRollAssignmentResponse from(ChequeChequesRoll l) {
            return new ChequeRollAssignmentResponse(
                    l.getId(), l.getChequeChequesId(), l.getChequeRollsId());
        }
    }
}
