/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.inventory;

import com.turquaz.core.money.Quantity;
import com.turquaz.persistence.inventory.InventoryCard;
import com.turquaz.persistence.inventory.InventoryWarehouse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public final class InventoryDtos {

    private InventoryDtos() {}

    public record InventoryCardResponse(
            UUID id, UUID companyId, String code, String name, String definition,
            Integer minimumAmount, Integer maximumAmount,
            Integer vatRate, Integer discountPercent, Integer specialVatRate) {

        public static InventoryCardResponse from(InventoryCard c) {
            return new InventoryCardResponse(
                    c.getId(), c.getCompanyId(),
                    c.getCardInventoryCode(), c.getCardName(), c.getCardDefinition(),
                    c.getCardMinimumAmount(), c.getCardMaximumAmount(),
                    c.getCardVat(), c.getCardDiscount(), c.getCardSpecialVat());
        }
    }

    public record CreateInventoryCardRequest(
            @NotBlank @Size(max = 25) String code,
            @NotBlank @Size(max = 250) String name,
            @NotBlank @Size(max = 250) String definition,
            @NotNull @PositiveOrZero Integer minimumAmount,
            @NotNull @PositiveOrZero Integer maximumAmount,
            @NotNull @PositiveOrZero Integer vatRate,
            @NotNull @PositiveOrZero Integer discountPercent,
            @NotNull @PositiveOrZero Integer specialVatRate) {
    }

    public record WarehouseResponse(
            UUID id, UUID companyId, String code, String name,
            String address, String city, String telephone, String description) {

        public static WarehouseResponse from(InventoryWarehouse w) {
            return new WarehouseResponse(
                    w.getId(), w.getCompanyId(),
                    w.getWarehousesCode(), w.getWarehousesName(),
                    w.getWarehousesAddress(), w.getWarehousesCity(),
                    w.getWarehousesTelephone(), w.getWarehousesDescription());
        }
    }

    public record CreateWarehouseRequest(
            @NotBlank @Size(max = 25) String code,
            @NotBlank @Size(max = 50) String name,
            @Size(max = 250) String address,
            @Size(max = 25) String city,
            @Size(max = 25) String telephone,
            @Size(max = 250) String description) {
    }

    public record StockOnHandResponse(
            UUID cardId, UUID warehouseId,
            BigDecimal amount,
            java.time.Instant asOf) {

        public static StockOnHandResponse of(
                UUID cardId, UUID warehouseId, Quantity q, java.time.Instant asOf) {
            return new StockOnHandResponse(cardId, warehouseId, q.value(), asOf);
        }
    }
}
