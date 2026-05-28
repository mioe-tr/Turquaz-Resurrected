/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class InventoryApi {

    private final RestClient client;

    public InventoryApi(RestClient client) { this.client = client; }

    public List<InventoryCard> listCards() {
        return client.get("/api/v1/inventory/cards",
                new TypeReference<List<InventoryCard>>() {});
    }

    public InventoryCard createCard(CreateCard req) {
        return client.post("/api/v1/inventory/cards", req, InventoryCard.class);
    }

    public List<Warehouse> listWarehouses() {
        return client.get("/api/v1/inventory/warehouses",
                new TypeReference<List<Warehouse>>() {});
    }

    public Warehouse createWarehouse(CreateWarehouse req) {
        return client.post("/api/v1/inventory/warehouses", req, Warehouse.class);
    }

    public StockOnHand stockOnHand(String cardId, String warehouseId, Instant asOf) {
        StringBuilder url = new StringBuilder(
                "/api/v1/inventory/ledger/stock?cardId=" + cardId + "&warehouseId=" + warehouseId);
        if (asOf != null) url.append("&asOf=").append(asOf);
        return client.get(url.toString(), StockOnHand.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryCard {
        public String id;
        public String companyId;
        public String code;
        public String name;
        public String definition;
        public Integer minimumAmount;
        public Integer maximumAmount;
        public Integer vatRate;
        public Integer discountPercent;
        public Integer specialVatRate;
    }

    public static class CreateCard {
        public String code;
        public String name;
        public String definition;
        public Integer minimumAmount;
        public Integer maximumAmount;
        public Integer vatRate;
        public Integer discountPercent;
        public Integer specialVatRate;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Warehouse {
        public String id;
        public String companyId;
        public String code;
        public String name;
        public String address;
        public String city;
        public String telephone;
        public String description;
    }

    public static class CreateWarehouse {
        public String code;
        public String name;
        public String address;
        public String city;
        public String telephone;
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockOnHand {
        public String cardId;
        public String warehouseId;
        public BigDecimal amount;
        public Instant asOf;
    }
}
