/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.current;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.current.CurrentCard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public final class CurrentCardDtos {

    private CurrentCardDtos() {}

    public record CurrentCardResponse(
            UUID id,
            UUID companyId,
            String code,
            String name,
            String definition,
            String address,
            String taxDepartment,
            String taxNumber,
            BigDecimal creditLimit,
            BigDecimal riskLimit,
            BigDecimal discountRate,
            BigDecimal discountPayment,
            Integer daysToValue) {

        public static CurrentCardResponse from(CurrentCard c) {
            return new CurrentCardResponse(
                    c.getId(), c.getCompanyId(),
                    c.getCardsCurrentCode(), c.getCardsName(), c.getCardsDefinition(),
                    c.getCardsAddress(),
                    c.getCardsTaxDepartment(), c.getCardsTaxNumber(),
                    c.getCardsCreditLimit(), c.getCardsRiskLimit(),
                    c.getCardsDiscountRate(), c.getCardsDiscountPayment(),
                    c.getDaysToValue());
        }
    }

    public record CreateCurrentCardRequest(
            @NotBlank @Size(max = 25) String code,
            @NotBlank @Size(max = 250) String name,
            @NotBlank @Size(max = 250) String definition,
            @NotBlank @Size(max = 250) String address,
            @NotBlank @Size(max = 50) String taxDepartment,
            @NotBlank @Size(max = 50) String taxNumber,
            @NotNull @PositiveOrZero BigDecimal creditLimit,
            @NotNull @PositiveOrZero BigDecimal riskLimit,
            @NotNull @PositiveOrZero BigDecimal discountRate,
            @NotNull @PositiveOrZero BigDecimal discountPayment,
            @PositiveOrZero Integer daysToValue) {

        public Money creditLimitMoney() { return Money.of(creditLimit); }
        public Money riskLimitMoney() { return Money.of(riskLimit); }
        public Money discountPaymentMoney() { return Money.of(discountPayment); }
    }

    public record UpdateLimitsRequest(
            @NotNull @PositiveOrZero BigDecimal creditLimit,
            @NotNull @PositiveOrZero BigDecimal riskLimit) {
    }
}
