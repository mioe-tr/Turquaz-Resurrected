/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.money;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CurrencyToWordsTest {

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            value = {
                "0|Sıfır TL",
                "1|Bir TL",
                "10|On TL",
                "11|On Bir TL",
                "20|Yirmi TL",
                "99|Doksan Dokuz TL",
                "100|Yüz TL",            // 'Bir Yüz' değil
                "101|Yüz Bir TL",
                "125|Yüz Yirmi Beş TL",
                "200|İki Yüz TL",
                "999|Dokuz Yüz Doksan Dokuz TL",
                "1000|Bin TL",           // 'Bir Bin' değil
                "1001|Bin Bir TL",
                "2000|İki Bin TL",
                "1234|Bin İki Yüz Otuz Dört TL",
                "1000000|Bir Milyon TL", // 'Bir' korunur (yalnız Bin'de düşer)
                "1234567|Bir Milyon İki Yüz Otuz Dört Bin Beş Yüz Altmış Yedi TL"
            })
    void tam_lira_tutarları(String amount, String expected) {
        assertThat(CurrencyToWords.inTurkish(new java.math.BigDecimal(amount)))
                .isEqualTo(expected);
    }

    @Test
    void kuruş_var() {
        assertThat(CurrencyToWords.inTurkish(Money.of("1234.56")))
                .isEqualTo("Bin İki Yüz Otuz Dört TL, Elli Altı Kr");
        assertThat(CurrencyToWords.inTurkish(Money.of("0.05")))
                .isEqualTo("Sıfır TL, Beş Kr");
    }

    @Test
    void negatif_tutar() {
        assertThat(CurrencyToWords.inTurkish(Money.of("-150.25")))
                .isEqualTo("Eksi Yüz Elli TL, Yirmi Beş Kr");
    }

    @Test
    void kuruşa_yuvarlama() {
        // HALF_EVEN: 1.235 → 1.24 (5 sonrası ortada, çift basamağa)
        assertThat(CurrencyToWords.inTurkish(Money.of("1.235")))
                .isEqualTo("Bir TL, Yirmi Dört Kr");
    }
}
