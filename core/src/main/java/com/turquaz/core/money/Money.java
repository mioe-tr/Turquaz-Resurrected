/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Para tutarı: {@code NUMERIC(19,4)} duyarlığında değişmez (immutable) değer
 * nesnesi. {@code double}/{@code float} kullanılmaz; tüm aritmetik
 * {@link BigDecimal} üzerinden HALF_EVEN (bankır yuvarlaması) ile yapılır.
 *
 * <p>Ölçek constructor'da 4'e sabitlenir; aritmetik sonuçlar yine 4'e
 * yuvarlanır. {@link #toBaseCurrency(BigDecimal)} kur çevirisi için yardımcıdır.
 */
public record Money(BigDecimal amount) implements Comparable<Money> {

    /** Veritabanı ölçeği (NUMERIC(19,4)) ile uyumlu. */
    public static final int SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "amount null olamaz");
        amount = amount.setScale(SCALE, ROUNDING);
    }

    public static Money of(String s) { return new Money(new BigDecimal(s)); }
    public static Money of(long v) { return new Money(BigDecimal.valueOf(v)); }
    public static Money of(BigDecimal v) { return new Money(v); }

    public Money add(Money other) { return new Money(amount.add(other.amount)); }
    public Money subtract(Money other) { return new Money(amount.subtract(other.amount)); }
    public Money multiply(BigDecimal factor) { return new Money(amount.multiply(factor)); }
    public Money negate() { return new Money(amount.negate()); }

    public boolean isZero() { return amount.signum() == 0; }
    public boolean isPositive() { return amount.signum() > 0; }
    public boolean isNegative() { return amount.signum() < 0; }

    public Money toBaseCurrency(BigDecimal exchangeRate) {
        return new Money(amount.multiply(exchangeRate));
    }

    @Override
    public int compareTo(Money other) { return amount.compareTo(other.amount); }

    @Override
    public String toString() { return amount.toPlainString(); }
}
