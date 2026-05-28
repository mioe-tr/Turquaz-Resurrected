/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AdminApi {

    private final RestClient client;

    public AdminApi(RestClient client) { this.client = client; }

    // ----- Kullanıcılar -----

    public List<UserSummary> listUsers() {
        return client.get("/api/v1/admin/users", new TypeReference<List<UserSummary>>() {});
    }

    public void changeMyPassword(String currentPassword, String newPassword) {
        client.put("/api/v1/admin/users/me/password",
                Map.of("currentPassword", currentPassword, "newPassword", newPassword),
                Void.class);
    }

    // ----- Şirket -----

    public CompanyInfo getCompany() {
        return client.get("/api/v1/admin/company", CompanyInfo.class);
    }

    public CompanyInfo updateCompany(UpdateCompany req) {
        return client.put("/api/v1/admin/company", req, CompanyInfo.class);
    }

    // ----- Para Birimleri -----

    public List<CurrencyDto> listCurrencies() {
        return client.get("/api/v1/currencies", new TypeReference<List<CurrencyDto>>() {});
    }

    public CurrencyDto createCurrency(CreateCurrency req) {
        return client.post("/api/v1/currencies", req, CurrencyDto.class);
    }

    // ----- Döviz Kurları -----

    public List<ExchangeRateDto> listExchangeRates() {
        return client.get("/api/v1/exchange-rates", new TypeReference<List<ExchangeRateDto>>() {});
    }

    public ExchangeRateDto createExchangeRate(CreateExchangeRate req) {
        return client.post("/api/v1/exchange-rates", req, ExchangeRateDto.class);
    }

    // ----- DTO'lar -----

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserSummary {
        public String id;
        public String username;
        public String realName;
        public String description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompanyInfo {
        public String id;
        public String name;
        public String address;
        public String telephone;
        public String fax;
    }

    public static class UpdateCompany {
        public String name;
        public String address;
        public String telephone;
        public String fax;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrencyDto {
        public String id;
        public String name;
        public String abbreviation;
        public String country;
        public Boolean defaultCurrency;
        public Boolean constant;
    }

    public static class CreateCurrency {
        public String name;
        public String abbreviation;
        public String country;
        public Boolean defaultCurrency;
        public Boolean constant;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateDto {
        public String id;
        public String baseCurrencyId;
        public String exchangeCurrencyId;
        public BigDecimal exchangeRatio;
        public LocalDate date;
    }

    public static class CreateExchangeRate {
        public String baseCurrencyId;
        public String exchangeCurrencyId;
        public BigDecimal exchangeRatio;
        public LocalDate date;
    }
}
