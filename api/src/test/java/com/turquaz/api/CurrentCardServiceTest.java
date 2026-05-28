/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Money;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.current.CurrentCard;
import com.turquaz.persistence.current.CurrentCardService;
import com.turquaz.persistence.current.NewCurrentCard;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurrentCardServiceTest {

    @Autowired CurrentCardService service;
    @Autowired CompanyRepository companies;

    private NewCurrentCard sample(UUID companyId, String code) {
        return new NewCurrentCard(
                companyId, code, "Test Müşteri", "Tanım", "Adres",
                "Beşiktaş VD", "1234567890",
                Money.of("10000"), Money.of("5000"),
                new BigDecimal("0.05"), Money.of("100"),
                30, "test");
    }

    @Test
    void kart_oluşturulur_ve_kodla_bulunur() {
        UUID companyId = companies.findAll().getFirst().getId();
        String code = "CARI-" + System.nanoTime();

        CurrentCard saved = service.create(sample(companyId, code));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCardsCurrentCode()).isEqualTo(code);

        CurrentCard found = service.findByCode(companyId, code);
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getCardsName()).isEqualTo("Test Müşteri");
        assertThat(found.getCardsCreditLimit()).isEqualByComparingTo("10000.0000");
    }

    @Test
    void aynı_kod_iki_kez_oluşturulamaz() {
        UUID companyId = companies.findAll().getFirst().getId();
        String code = "DUP-" + System.nanoTime();
        service.create(sample(companyId, code));
        assertThatThrownBy(() -> service.create(sample(companyId, code)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    @Test
    void bulunmayan_kod_için_anlamlı_hata() {
        UUID companyId = companies.findAll().getFirst().getId();
        assertThatThrownBy(() -> service.findByCode(companyId, "YOK"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("bulunamadı");
    }

    @Test
    void limit_güncelleme() {
        UUID companyId = companies.findAll().getFirst().getId();
        CurrentCard saved = service.create(sample(companyId, "LIM-" + System.nanoTime()));
        CurrentCard updated = service.updateLimits(
                saved.getId(), Money.of("99999"), Money.of("50000"), "admin");
        assertThat(updated.getCardsCreditLimit()).isEqualByComparingTo("99999.0000");
        assertThat(updated.getCardsRiskLimit()).isEqualByComparingTo("50000.0000");
    }
}
