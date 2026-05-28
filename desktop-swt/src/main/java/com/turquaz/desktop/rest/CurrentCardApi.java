/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CurrentCardApi {

    private final RestClient client;

    public CurrentCardApi(RestClient client) {
        this.client = client;
    }

    public List<CurrentCard> list() {
        return client.get("/api/v1/current-cards", new TypeReference<List<CurrentCard>>() {});
    }

    public CurrentCard findByCode(String code) {
        return client.get("/api/v1/current-cards/by-code/" + code, CurrentCard.class);
    }

    public CurrentCard create(CreateRequest req) {
        return client.post("/api/v1/current-cards", req, CurrentCard.class);
    }

    public CurrentCard updateLimits(String id, BigDecimal creditLimit, BigDecimal riskLimit) {
        return client.put("/api/v1/current-cards/" + id + "/limits",
                Map.of("creditLimit", creditLimit, "riskLimit", riskLimit),
                CurrentCard.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentCard {
        public String id;
        public String companyId;
        public String code;
        public String name;
        public String definition;
        public String address;
        public String taxDepartment;
        public String taxNumber;
        public BigDecimal creditLimit;
        public BigDecimal riskLimit;
        public BigDecimal discountRate;
        public BigDecimal discountPayment;
        public Integer daysToValue;
    }

    public static class CreateRequest {
        public String code;
        public String name;
        public String definition;
        public String address;
        public String taxDepartment;
        public String taxNumber;
        public BigDecimal creditLimit;
        public BigDecimal riskLimit;
        public BigDecimal discountRate;
        public BigDecimal discountPayment;
        public Integer daysToValue;
    }
}
