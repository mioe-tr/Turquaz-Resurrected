/*
 * Turquaz Resurrected — GPLv3
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Fatura + Sipariş + Konsinye + Profit raporu. */
public class BillApi {

    private final RestClient client;

    public BillApi(RestClient client) { this.client = client; }

    public List<Bill> listBills() {
        return client.get("/api/v1/bills", new TypeReference<List<Bill>>() {});
    }

    public Bill createBill(CreateBill r) {
        return client.post("/api/v1/bills", r, Bill.class);
    }

    public Bill printBill(String id) {
        return client.post("/api/v1/bills/" + id + "/print", null, Bill.class);
    }

    public Bill closeBill(String id) {
        return client.post("/api/v1/bills/" + id + "/close", null, Bill.class);
    }

    public List<Order> listOrders() {
        return client.get("/api/v1/orders", new TypeReference<List<Order>>() {});
    }

    public Order createOrder(CreateOrder r) {
        return client.post("/api/v1/orders", r, Order.class);
    }

    public Order deliverOrder(String id) {
        return client.post("/api/v1/orders/" + id + "/deliver", null, Order.class);
    }

    public List<Consignment> listConsignments() {
        return client.get("/api/v1/consignments", new TypeReference<List<Consignment>>() {});
    }

    public Consignment createConsignment(CreateConsignment r) {
        return client.post("/api/v1/consignments", r, Consignment.class);
    }

    public Consignment printConsignment(String id) {
        return client.post("/api/v1/consignments/" + id + "/print", null, Consignment.class);
    }

    public List<InventoryProfitRow> inventoryProfit(String from, String to) {
        String url = "/api/v1/reports/inventory-profit?from=" + from + "&to=" + to;
        return client.get(url, new TypeReference<List<InventoryProfitRow>>() {});
    }

    // ----- Records -----

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bill {
        public String id;
        public String companyId;
        public Integer type;
        public String documentNo;
        public String definition;
        public LocalDate billDate;
        public LocalDate dueDate;
        public String currentCardId;
        public Boolean printed;
        public Boolean open;
    }

    public static class CreateBill {
        public Integer type;
        public LocalDate billDate;
        public LocalDate dueDate;
        public String documentNo;
        public String definition;
        public String currentCardId;
        public String exchangeRateId;
        public String engineSequenceId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order {
        public String id;
        public String companyId;
        public Integer type;
        public Integer documentNo;
        public String definition;
        public LocalDate orderDate;
        public LocalDate dueDate;
        public LocalDate deliverDate;
        public String currentCardId;
        public String billId;
        public BigDecimal totalAmount;
        public Boolean delivered;
    }

    public static class CreateOrder {
        public Integer type;
        public Integer documentNo;
        public LocalDate orderDate;
        public LocalDate dueDate;
        public LocalDate deliverDate;
        public String currentCardId;
        public String billId;
        public String definition;
        public Integer discountRatePercent;
        public Integer vatPercent;
        public BigDecimal discountAmount;
        public BigDecimal charges;
        public BigDecimal vatAmount;
        public BigDecimal totalAmount;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Consignment {
        public String id;
        public String companyId;
        public Integer type;
        public String documentNo;
        public String referenceBillNo;
        public String definition;
        public LocalDate date;
        public String currentCardId;
        public Boolean printed;
    }

    public static class CreateConsignment {
        public Integer type;
        public LocalDate date;
        public String documentNo;
        public String referenceBillNo;
        public String definition;
        public String currentCardId;
        public String exchangeRateId;
        public String engineSequenceId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryProfitRow {
        public String cardId;
        public BigDecimal amountIn;
        public BigDecimal amountOut;
        public BigDecimal costIn;
        public BigDecimal revenueOut;
        public BigDecimal avgUnitCost;
        public BigDecimal costOfSold;
        public BigDecimal profit;
    }
}
