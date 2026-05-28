/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.bill;

import com.turquaz.api.bill.BillDtos.BillResponse;
import com.turquaz.api.bill.BillDtos.ConsignmentResponse;
import com.turquaz.api.bill.BillDtos.CreateBillRequest;
import com.turquaz.api.bill.BillDtos.CreateConsignmentRequest;
import com.turquaz.api.bill.BillDtos.CreateOrderRequest;
import com.turquaz.api.bill.BillDtos.OrderResponse;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.persistence.bill.BillService;
import com.turquaz.persistence.bill.NewBill;
import com.turquaz.persistence.bill.NewOrder;
import com.turquaz.persistence.bill.OrderService;
import com.turquaz.persistence.consignment.ConsignmentService;
import com.turquaz.persistence.consignment.NewConsignment;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BillController {

    private final BillService billService;
    private final OrderService orderService;
    private final ConsignmentService consignmentService;

    public BillController(
            BillService billService,
            OrderService orderService,
            ConsignmentService consignmentService) {
        this.billService = billService;
        this.orderService = orderService;
        this.consignmentService = consignmentService;
    }

    // -------------------- Fatura --------------------

    @GetMapping("/bills")
    public List<BillResponse> listBills() {
        return billService.listAll(CurrentUser.companyId()).stream()
                .map(BillResponse::from).toList();
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return orderService.listAll(CurrentUser.companyId()).stream()
                .map(OrderResponse::from).toList();
    }

    @GetMapping("/consignments")
    public List<ConsignmentResponse> listConsignments() {
        return consignmentService.listAll(CurrentUser.companyId()).stream()
                .map(ConsignmentResponse::from).toList();
    }

    @PostMapping("/bills")
    public ResponseEntity<BillResponse> createBill(@Valid @RequestBody CreateBillRequest req) {
        var saved = billService.create(new NewBill(
                CurrentUser.companyId(),
                req.type(), req.billDate(), req.dueDate(),
                req.documentNo(), req.definition(),
                req.currentCardId(), req.exchangeRateId(), req.engineSequenceId(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(BillResponse.from(saved));
    }

    @PostMapping("/bills/{id}/print")
    public BillResponse markPrinted(@PathVariable UUID id) {
        return BillResponse.from(billService.markPrinted(id, CurrentUser.username()));
    }

    @PostMapping("/bills/{id}/close")
    public BillResponse close(@PathVariable UUID id) {
        return BillResponse.from(billService.close(id, CurrentUser.username()));
    }

    // -------------------- Sipariş --------------------

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest req) {
        var saved = orderService.create(new NewOrder(
                CurrentUser.companyId(),
                req.type(), req.documentNo(),
                req.orderDate(), req.dueDate(), req.deliverDate(),
                req.currentCardId(), req.billId(), req.definition(),
                req.discountRatePercent(), req.vatPercent(),
                req.discountAmountMoney(), req.chargesMoney(),
                req.vatAmountMoney(), req.totalAmountMoney(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(OrderResponse.from(saved));
    }

    @PostMapping("/orders/{id}/deliver")
    public OrderResponse deliver(@PathVariable UUID id) {
        return OrderResponse.from(orderService.markDelivered(id, CurrentUser.username()));
    }

    // -------------------- Konsinye --------------------

    @PostMapping("/consignments")
    public ResponseEntity<ConsignmentResponse> createConsignment(
            @Valid @RequestBody CreateConsignmentRequest req) {
        var saved = consignmentService.create(new NewConsignment(
                CurrentUser.companyId(),
                req.type(), req.date(),
                req.documentNo(), req.referenceBillNo(), req.definition(),
                req.currentCardId(), req.exchangeRateId(), req.engineSequenceId(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(ConsignmentResponse.from(saved));
    }

    @PostMapping("/consignments/{id}/print")
    public ConsignmentResponse markConsignmentPrinted(@PathVariable UUID id) {
        return ConsignmentResponse.from(consignmentService.markPrinted(id, CurrentUser.username()));
    }
}
