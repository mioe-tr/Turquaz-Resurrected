/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.money;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void inşa_dört_ondalığa_yuvarlar() {
        assertThat(Money.of("1.23456").amount()).isEqualByComparingTo("1.2346");
        assertThat(Money.of("1.234500001").amount()).isEqualByComparingTo("1.2345");
        // HALF_EVEN: 1.23455 → 1.2346 (5 sonrası ortada → çift basamağa)
        assertThat(Money.of("1.23455").amount()).isEqualByComparingTo("1.2346");
        // 1.23445 → 1.2344 (yine çift basamağa)
        assertThat(Money.of("1.23445").amount()).isEqualByComparingTo("1.2344");
    }

    @Test
    void toplama_ve_çıkarma() {
        Money a = Money.of("10.0000");
        Money b = Money.of("3.5000");
        assertThat(a.add(b).amount()).isEqualByComparingTo("13.5000");
        assertThat(a.subtract(b).amount()).isEqualByComparingTo("6.5000");
        assertThat(a.subtract(a).isZero()).isTrue();
    }

    @Test
    void kur_çevirisi() {
        Money tl = Money.of("100");
        Money usd = tl.toBaseCurrency(new BigDecimal("0.0345"));
        assertThat(usd.amount()).isEqualByComparingTo("3.4500");
    }

    @Test
    void karşılaştırma() {
        assertThat(Money.of("5").compareTo(Money.of("3"))).isPositive();
        assertThat(Money.of("3").compareTo(Money.of("5"))).isNegative();
        assertThat(Money.of("5").compareTo(Money.of("5"))).isZero();
    }
}
