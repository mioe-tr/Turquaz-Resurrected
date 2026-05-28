/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.common;

import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.BusinessRuleException;
import com.turquaz.persistence.common.Currency;
import com.turquaz.persistence.common.CurrencyExchangeRate;
import com.turquaz.persistence.common.CurrencyExchangeRateRepository;
import com.turquaz.persistence.common.CurrencyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CurrencyController {

    private final CurrencyRepository currencies;
    private final CurrencyExchangeRateRepository rates;

    public CurrencyController(
            CurrencyRepository currencies,
            CurrencyExchangeRateRepository rates) {
        this.currencies = currencies;
        this.rates = rates;
    }

    // ----- Para birimleri -----

    public record CurrencyDto(
            UUID id, String name, String abbreviation, String country,
            Boolean defaultCurrency, Boolean constant) {
        static CurrencyDto from(Currency c) {
            return new CurrencyDto(c.getId(), c.getCurrenciesName(),
                    c.getCurrenciesAbbreviation(), c.getCurrenciesCountry(),
                    c.getDefaultCurrency(), c.getConstant());
        }
    }

    public record CreateCurrencyRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 10) String abbreviation,
            @NotBlank @Size(max = 100) String country,
            Boolean defaultCurrency,
            Boolean constant) {
    }

    @GetMapping("/currencies")
    public List<CurrencyDto> listCurrencies() {
        return currencies.findByCompanyId(CurrentUser.companyId()).stream()
                .map(CurrencyDto::from)
                .toList();
    }

    @PostMapping("/currencies")
    @Transactional
    public ResponseEntity<CurrencyDto> createCurrency(
            @Valid @RequestBody CreateCurrencyRequest req) {
        Currency c = new Currency();
        c.setCompanyId(CurrentUser.companyId());
        c.setCurrenciesName(req.name());
        c.setCurrenciesAbbreviation(req.abbreviation());
        c.setCurrenciesCountry(req.country());
        c.setDefaultCurrency(Boolean.TRUE.equals(req.defaultCurrency()));
        c.setConstant(Boolean.TRUE.equals(req.constant()));
        c.setCreatedBy(CurrentUser.username());
        c.setUpdatedBy(CurrentUser.username());
        return ResponseEntity.status(201).body(CurrencyDto.from(currencies.save(c)));
    }

    // ----- Döviz kurları -----

    public record ExchangeRateDto(
            UUID id, UUID baseCurrencyId, UUID exchangeCurrencyId,
            BigDecimal exchangeRatio, LocalDate date) {
        static ExchangeRateDto from(CurrencyExchangeRate r) {
            return new ExchangeRateDto(
                    r.getId(),
                    r.getBaseCurrencyId(),
                    r.getExchangeCurrencyId(),
                    r.getExchangeRatio(),
                    r.getExhangeRatesDate() == null ? null
                            : r.getExhangeRatesDate().atZone(ZoneOffset.UTC).toLocalDate());
        }
    }

    public record CreateExchangeRateRequest(
            @NotNull UUID baseCurrencyId,
            @NotNull UUID exchangeCurrencyId,
            @NotNull @Positive BigDecimal exchangeRatio,
            @NotNull LocalDate date) {
    }

    @GetMapping("/exchange-rates")
    public List<ExchangeRateDto> listExchangeRates() {
        return rates.findByCompanyIdOrderByExhangeRatesDateDesc(CurrentUser.companyId()).stream()
                .map(ExchangeRateDto::from)
                .toList();
    }

    @PostMapping("/exchange-rates")
    @Transactional
    public ResponseEntity<ExchangeRateDto> createExchangeRate(
            @Valid @RequestBody CreateExchangeRateRequest req) {
        if (req.baseCurrencyId().equals(req.exchangeCurrencyId())) {
            throw new BusinessRuleException("Baz ve hedef para birimi aynı olamaz");
        }
        CurrencyExchangeRate r = new CurrencyExchangeRate();
        r.setCompanyId(CurrentUser.companyId());
        r.setBaseCurrencyId(req.baseCurrencyId());
        r.setExchangeCurrencyId(req.exchangeCurrencyId());
        r.setExchangeRatio(req.exchangeRatio());
        r.setExhangeRatesDate(req.date().atStartOfDay(ZoneOffset.UTC).toInstant());
        return ResponseEntity.status(201).body(ExchangeRateDto.from(rates.save(r)));
    }
}
