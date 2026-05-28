/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.bill;

import com.turquaz.core.money.Money;
import com.turquaz.persistence.bill.Bill;
import com.turquaz.persistence.bill.Order;
import com.turquaz.persistence.consignment.Consignment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class BillDtos {

    private BillDtos() {}

    public record BillResponse(
            UUID id, UUID companyId, Integer type, String documentNo,
            String definition, LocalDate billDate, LocalDate dueDate,
            UUID currentCardId, Boolean printed, Boolean open) {

        public static BillResponse from(Bill b) {
            return new BillResponse(
                    b.getId(), b.getCompanyId(), b.getBillsType(), b.getBillDocumentNo(),
                    b.getBillsDefinition(),
                    b.getBillsDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    b.getDueDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    b.getCurrentCardsId(), b.getBillsPrinted(), b.getIsOpen());
        }
    }

    public record CreateBillRequest(
            @NotNull @Min(1) @Max(2) Integer type,
            @NotNull LocalDate billDate,
            @NotNull LocalDate dueDate,
            @NotBlank @Size(max = 50) String documentNo,
            @NotBlank @Size(max = 250) String definition,
            @NotNull UUID currentCardId,
            @NotNull UUID exchangeRateId,
            @NotNull UUID engineSequenceId) {
    }

    public record OrderResponse(
            UUID id, UUID companyId, Integer type, Integer documentNo,
            String definition, LocalDate orderDate, LocalDate dueDate,
            LocalDate deliverDate, UUID currentCardId, UUID billId,
            BigDecimal totalAmount, Boolean delivered) {

        public static OrderResponse from(Order o) {
            return new OrderResponse(
                    o.getId(), o.getCompanyId(), o.getOrdersType(), o.getOrdersDocumentNo(),
                    o.getOrdersDefinition(),
                    o.getOrdersDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    o.getOrdersDueDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    o.getOrdersDeliverDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    o.getCurrentCardsId(), o.getBillsId(),
                    o.getOrdersTotalAmount(),
                    o.getOrdersDelivered() != null && o.getOrdersDelivered() == 1);
        }
    }

    public record CreateOrderRequest(
            @NotNull @Min(1) @Max(2) Integer type,
            @NotNull Integer documentNo,
            @NotNull LocalDate orderDate,
            @NotNull LocalDate dueDate,
            @NotNull LocalDate deliverDate,
            @NotNull UUID currentCardId,
            @NotNull UUID billId,
            @NotBlank @Size(max = 250) String definition,
            @NotNull @PositiveOrZero Integer discountRatePercent,
            @NotNull @PositiveOrZero Integer vatPercent,
            @NotNull @PositiveOrZero BigDecimal discountAmount,
            @NotNull @PositiveOrZero BigDecimal charges,
            @NotNull @PositiveOrZero BigDecimal vatAmount,
            @NotNull @PositiveOrZero BigDecimal totalAmount) {

        public Money discountAmountMoney() { return Money.of(discountAmount); }
        public Money chargesMoney() { return Money.of(charges); }
        public Money vatAmountMoney() { return Money.of(vatAmount); }
        public Money totalAmountMoney() { return Money.of(totalAmount); }
    }

    public record ConsignmentResponse(
            UUID id, UUID companyId, Integer type, String documentNo,
            String referenceBillNo, String definition,
            LocalDate date, UUID currentCardId, Boolean printed) {

        public static ConsignmentResponse from(Consignment c) {
            return new ConsignmentResponse(
                    c.getId(), c.getCompanyId(), c.getConsignmentsType(),
                    c.getConsignmentDocumentNo(), c.getBillDocumentNo(),
                    c.getConsignmentsDefinition(),
                    c.getConsignmentsDate().atZone(java.time.ZoneOffset.UTC).toLocalDate(),
                    c.getCurrentCardsId(), c.getConsignmentsPrinted());
        }
    }

    public record CreateConsignmentRequest(
            @NotNull @Min(1) @Max(2) Integer type,
            @NotNull LocalDate date,
            @NotBlank @Size(max = 50) String documentNo,
            @NotBlank @Size(max = 50) String referenceBillNo,
            @NotBlank @Size(max = 250) String definition,
            @NotNull UUID currentCardId,
            @NotNull UUID exchangeRateId,
            @NotNull UUID engineSequenceId) {
    }
}
