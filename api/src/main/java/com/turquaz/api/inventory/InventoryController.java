/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.inventory;

import com.turquaz.api.inventory.InventoryDtos.CreateInventoryCardRequest;
import com.turquaz.api.inventory.InventoryDtos.CreateWarehouseRequest;
import com.turquaz.api.inventory.InventoryDtos.InventoryCardResponse;
import com.turquaz.api.inventory.InventoryDtos.StockOnHandResponse;
import com.turquaz.api.inventory.InventoryDtos.WarehouseResponse;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.persistence.inventory.InventoryCardService;
import com.turquaz.persistence.inventory.InventoryLedgerService;
import com.turquaz.persistence.inventory.InventoryWarehouseService;
import com.turquaz.persistence.inventory.NewInventoryCard;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryCardService cards;
    private final InventoryWarehouseService warehouses;
    private final InventoryLedgerService ledger;

    public InventoryController(
            InventoryCardService cards,
            InventoryWarehouseService warehouses,
            InventoryLedgerService ledger) {
        this.cards = cards;
        this.warehouses = warehouses;
        this.ledger = ledger;
    }

    // --- Stok kartları ---

    @GetMapping("/cards")
    public List<InventoryCardResponse> listCards() {
        return cards.listAll(CurrentUser.companyId()).stream()
                .map(InventoryCardResponse::from).toList();
    }

    @GetMapping("/cards/by-code/{code}")
    public InventoryCardResponse cardByCode(@PathVariable String code) {
        return InventoryCardResponse.from(cards.findByCode(CurrentUser.companyId(), code));
    }

    @PostMapping("/cards")
    public ResponseEntity<InventoryCardResponse> createCard(
            @Valid @RequestBody CreateInventoryCardRequest req) {
        var saved = cards.create(new NewInventoryCard(
                CurrentUser.companyId(),
                req.code(), req.name(), req.definition(),
                req.minimumAmount(), req.maximumAmount(),
                req.vatRate(), req.discountPercent(), req.specialVatRate(),
                CurrentUser.username()));
        return ResponseEntity.status(201).body(InventoryCardResponse.from(saved));
    }

    // --- Depolar ---

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses() {
        return warehouses.listAll(CurrentUser.companyId()).stream()
                .map(WarehouseResponse::from).toList();
    }

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponse> createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest req) {
        var saved = warehouses.create(
                CurrentUser.companyId(),
                req.code(), req.name(), req.address(),
                req.city(), req.telephone(), req.description(),
                CurrentUser.username());
        return ResponseEntity.status(201).body(WarehouseResponse.from(saved));
    }

    // --- Stok defteri (bakiye) ---

    @GetMapping("/ledger/stock")
    public StockOnHandResponse stockOnHand(
            @RequestParam UUID cardId,
            @RequestParam UUID warehouseId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant asOf) {
        Instant effective = (asOf != null) ? asOf : Instant.now();
        var qty = ledger.stockOnHand(CurrentUser.companyId(), cardId, warehouseId, effective);
        return StockOnHandResponse.of(cardId, warehouseId, qty, effective);
    }
}
