/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.accounting;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.accounting.TrialBalanceLine;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class AccountingDtos {

    private AccountingDtos() {}

    public record JournalLineRequest(
            @NotNull UUID accountId,
            @NotNull @PositiveOrZero BigDecimal debit,
            @NotNull @PositiveOrZero BigDecimal credit,
            @NotBlank @Size(max = 250) String description) {

        public Money debitMoney() { return Money.of(debit); }
        public Money creditMoney() { return Money.of(credit); }
    }

    public record PostJournalRequest(
            @NotNull LocalDate transactionDate,
            @NotBlank @Size(max = 50) String documentNo,
            @NotBlank @Size(max = 250) String description,
            @NotNull UUID journalId,
            @NotNull UUID transactionTypeId,
            @NotNull UUID moduleId,
            @NotNull UUID engineSequenceId,
            @NotNull UUID exchangeRateId,
            @NotNull BigDecimal exchangeRate,
            @NotEmpty @Valid List<JournalLineRequest> lines) {
    }

    public record JournalEntryResponse(
            UUID transactionId,
            UUID journalId,
            LocalDate transactionDate,
            String documentNo,
            String description) {

        public static JournalEntryResponse from(AccountingTransaction tx) {
            return new JournalEntryResponse(
                    tx.getId(), tx.getAccountingJournalId(),
                    tx.getTransactionsDate()
                            .atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    tx.getTransactionDocumentNo(), tx.getTransactionDescription());
        }
    }

    public record TrialBalanceLineResponse(
            UUID accountId,
            BigDecimal totalDebit,
            BigDecimal totalCredit,
            BigDecimal net) {

        public static TrialBalanceLineResponse from(TrialBalanceLine l) {
            return new TrialBalanceLineResponse(
                    l.accountId(),
                    l.totalDebit().amount(),
                    l.totalCredit().amount(),
                    l.net().amount());
        }
    }

    public record AccountBalanceResponse(
            UUID accountId,
            BigDecimal totalDebit,
            BigDecimal totalCredit,
            BigDecimal net,
            Instant asOf) {

        public static AccountBalanceResponse from(TrialBalanceLine l, Instant asOf) {
            return new AccountBalanceResponse(
                    l.accountId(),
                    l.totalDebit().amount(),
                    l.totalCredit().amount(),
                    l.net().amount(),
                    asOf);
        }
    }
}
