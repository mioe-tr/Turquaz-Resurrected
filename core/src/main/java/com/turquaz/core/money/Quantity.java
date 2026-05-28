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
 * Miktar / oran: {@code NUMERIC(19,6)} duyarlığında değişmez değer nesnesi.
 * Stok miktarı (kg, lt, adet), birim çevrim katsayısı, döviz kuru oranı gibi
 * para olmayan sayısal alanlar için.
 */
public record Quantity(BigDecimal value) implements Comparable<Quantity> {

    public static final int SCALE = 6;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public static final Quantity ZERO = new Quantity(BigDecimal.ZERO);

    public Quantity {
        Objects.requireNonNull(value, "value null olamaz");
        value = value.setScale(SCALE, ROUNDING);
    }

    public static Quantity of(String s) { return new Quantity(new BigDecimal(s)); }
    public static Quantity of(long v) { return new Quantity(BigDecimal.valueOf(v)); }
    public static Quantity of(BigDecimal v) { return new Quantity(v); }

    public Quantity add(Quantity other) { return new Quantity(value.add(other.value)); }
    public Quantity subtract(Quantity other) { return new Quantity(value.subtract(other.value)); }
    public Quantity multiply(BigDecimal factor) { return new Quantity(value.multiply(factor)); }

    public boolean isZero() { return value.signum() == 0; }
    public boolean isPositive() { return value.signum() > 0; }
    public boolean isNegative() { return value.signum() < 0; }

    /** Miktar × birim fiyat → para. */
    public Money times(Money unitPrice) {
        return new Money(value.multiply(unitPrice.amount()));
    }

    @Override
    public int compareTo(Quantity other) { return value.compareTo(other.value); }

    @Override
    public String toString() { return value.toPlainString(); }
}
