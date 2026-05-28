/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.bank;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.accounting.AccountingTransaction;
import com.turquaz.persistence.bank.BanksCard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class BankDtos {

    private BankDtos() {}

    public record BankCardResponse(
            UUID id, UUID companyId, String code, String bankName,
            String branchName, String accountNo, String definition,
            UUID currencyId) {

        public static BankCardResponse from(BanksCard c) {
            return new BankCardResponse(
                    c.getId(), c.getCompanyId(),
                    c.getBankCode(), c.getBankName(),
                    c.getBankBranchName(), c.getBankAccountNo(),
                    c.getBankDefinition(), c.getCurrenciesId());
        }
    }

    public record CreateBankCardRequest(
            @NotBlank @Size(max = 100) String code,
            @NotBlank @Size(max = 50) String bankName,
            @NotBlank @Size(max = 50) String branchName,
            @NotBlank @Size(max = 50) String accountNo,
            @NotBlank @Size(max = 250) String definition,
            @NotNull UUID currencyId) {
    }

    public record BankMovementRequest(
            @NotNull UUID bankCardId,
            @NotNull UUID bankAccountingAccountId,
            @NotNull UUID counterAccountId,
            @NotNull UUID bankTransactionsBillsId,
            @NotNull @Positive BigDecimal amount,
            @NotNull LocalDate date,
            @NotBlank @Size(max = 50) String documentNo,
            @NotBlank @Size(max = 250) String description,
            // Yevmiye postlama bağlamı
            @NotNull UUID journalId,
            @NotNull UUID transactionTypeId,
            @NotNull UUID moduleId,
            @NotNull UUID engineSequenceId,
            @NotNull UUID exchangeRateId,
            @NotNull BigDecimal exchangeRate) {

        public Money amountMoney() { return Money.of(amount); }
    }

    public record BankMovementResponse(
            UUID accountingTransactionId, String documentNo, BigDecimal amount, String direction) {

        public static BankMovementResponse from(AccountingTransaction tx, Money amount, String direction) {
            return new BankMovementResponse(tx.getId(), tx.getTransactionDocumentNo(), amount.amount(), direction);
        }
    }
}
