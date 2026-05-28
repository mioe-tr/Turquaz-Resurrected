/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.turquaz.persistence.accounting.AccountingAccountRepository;
import com.turquaz.persistence.common.CompanyRepository;
import com.turquaz.persistence.engine.EngineMenuRepository;
import com.turquaz.persistence.engine.ModuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tam yığın doğrulama:
 * <ul>
 *   <li>Flyway V1__sema.sql + V2__seed.sql uygulanabiliyor mu?</li>
 *   <li>Hibernate {@code ddl-auto=validate} ile 79 entity şemaya uyuyor mu?</li>
 *   <li>2005 demo verisi (461 hesap, 141 menü, 10 modül, 1 şirket) yüklendi mi?</li>
 * </ul>
 *
 * <p>Gerçek PostgreSQL örneği gerektirir. CI'da {@code services.postgres} ile
 * sağlanır; yerel çalıştırma için {@code TURQUAZ_DB_*} ortam değişkenleri.
 */
@SpringBootTest
class SchemaValidationTest {

    @Autowired CompanyRepository companies;
    @Autowired EngineMenuRepository menus;
    @Autowired ModuleRepository modules;
    @Autowired AccountingAccountRepository accounts;

    @Test
    void context_yüklenmeli_ve_seed_uygulanmalı() {
        // Hibernate validate başarısız olsaydı context yüklenmezdi → bu noktaya gelmek geçer demek.
        assertThat(companies.count()).isGreaterThanOrEqualTo(1);
        assertThat(modules.count()).isEqualTo(10);
        assertThat(menus.count()).isEqualTo(141);
        assertThat(accounts.count()).isEqualTo(461);
    }
}
