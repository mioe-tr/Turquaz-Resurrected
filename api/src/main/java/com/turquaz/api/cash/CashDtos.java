/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.cash;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.cash.CashCard;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class CashDtos {

    private CashDtos() {}

    public record CashCardResponse(
            UUID id, UUID companyId, String name, String definition, UUID accountingAccountsId) {

        public static CashCardResponse from(CashCard c) {
            return new CashCardResponse(
                    c.getId(), c.getCompanyId(),
                    c.getCashCardName(), c.getCashCardDefinition(),
                    c.getAccountingAccountsId());
        }
    }

    public record CreateCashCardRequest(
            @NotBlank @Size(max = 250) String name,
            @NotBlank @Size(max = 250) String definition,
            @NotNull UUID accountingAccountsId) {
    }

    public record CashRowRequest(
            @NotNull UUID counterAccountId,
            @NotNull @PositiveOrZero BigDecimal debit,
            @NotNull @PositiveOrZero BigDecimal credit,
            @NotBlank @Size(max = 250) String description) {

        public Money debitMoney() { return Money.of(debit); }
        public Money creditMoney() { return Money.of(credit); }
    }

    public record CashMovementRequest(
            @NotNull UUID cashCardId,
            @NotNull UUID cashTransactionTypeId,
            @NotNull LocalDate date,
            @NotBlank @Size(max = 100) String documentNo,
            @NotBlank @Size(max = 250) String description,
            @NotEmpty @Valid List<CashRowRequest> rows,
            // Yevmiye postlama bağlamı
            @NotNull UUID journalId,
            @NotNull UUID transactionTypeId,
            @NotNull UUID moduleId,
            @NotNull UUID engineSequenceId,
            @NotNull UUID exchangeRateId,
            @NotNull BigDecimal exchangeRate) {
    }

    public record CashMovementResponse(UUID accountingTransactionId, String documentNo) {
        public static CashMovementResponse from(AccountingTransaction tx) {
            return new CashMovementResponse(tx.getId(), tx.getTransactionDocumentNo());
        }
    }
}
