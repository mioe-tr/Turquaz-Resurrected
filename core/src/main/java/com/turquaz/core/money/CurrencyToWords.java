/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Türk Lirası tutarını Türkçe kelimelerle ifade eder.
 * <p>Örn: {@code 1234.56} → {@code "Bin İki Yüz Otuz Dört TL, Elli Altı Kr"}.
 *
 * <p>Eski {@code com.turquaz.engine.bl.EngBLCurrencyToWords} sınıfının modern
 * yeniden yazımı: sayı parçalama {@link BigDecimal} üzerinden güvenli yapılır
 * ({@code toString().split(".")} gibi kırılgan yöntemler yerine), negatifler
 * desteklenir, {@code Locale}-bağımsız.
 */
public final class CurrencyToWords {

    private static final String[] ONES = {
            "", "Bir", "İki", "Üç", "Dört", "Beş", "Altı", "Yedi", "Sekiz", "Dokuz"
    };
    private static final String[] TENS = {
            "", "On", "Yirmi", "Otuz", "Kırk", "Elli", "Altmış", "Yetmiş", "Seksen", "Doksan"
    };
    /** Bin → 10^3, Milyon → 10^6, ... */
    private static final String[] SCALES = {
            "", "Bin", "Milyon", "Milyar", "Trilyon", "Katrilyon", "Kentilyon"
    };

    private CurrencyToWords() {}

    public static String inTurkish(Money money) {
        return inTurkish(money.amount());
    }

    public static String inTurkish(BigDecimal amount) {
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_EVEN);
        boolean negative = scaled.signum() < 0;
        BigDecimal abs = scaled.abs();
        long lira = abs.toBigInteger().longValueExact();
        long kurus = abs.movePointRight(2).toBigInteger().mod(java.math.BigInteger.valueOf(100)).longValueExact();

        String prefix = negative ? "Eksi " : "";
        String liraWords = convert(lira) + " TL";
        if (kurus == 0) {
            return (prefix + liraWords).trim();
        }
        return (prefix + liraWords + ", " + convert(kurus) + " Kr").trim();
    }

    private static String convert(long n) {
        if (n == 0) return "Sıfır";
        StringBuilder result = new StringBuilder();
        int scaleIndex = 0;
        while (n > 0) {
            int chunk = (int) (n % 1000);
            if (chunk != 0) {
                String chunkWords = convertLessThanThousand(chunk);
                String separator = result.isEmpty() ? "" : " ";
                // Türkçe: "Bir Bin" yerine "Bin"; ama "Bir Milyon", "Bir Milyar" geçerli.
                if (scaleIndex == 1 && chunk == 1) {
                    result.insert(0, SCALES[scaleIndex] + separator);
                } else if (scaleIndex > 0) {
                    result.insert(0, chunkWords + " " + SCALES[scaleIndex] + separator);
                } else {
                    result.insert(0, chunkWords);
                }
            }
            n /= 1000;
            scaleIndex++;
        }
        return result.toString().replaceAll("\\s+", " ").trim();
    }

    private static String convertLessThanThousand(int n) {
        StringBuilder sb = new StringBuilder();
        int hundreds = n / 100;
        int rem = n % 100;
        if (hundreds > 0) {
            // Türkçe: "Bir Yüz" yerine "Yüz"
            sb.append(hundreds == 1 ? "Yüz" : ONES[hundreds] + " Yüz");
        }
        int tens = rem / 10;
        int ones = rem % 10;
        if (tens > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(TENS[tens]);
        }
        if (ones > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(ONES[ones]);
        }
        return sb.toString();
    }
}
