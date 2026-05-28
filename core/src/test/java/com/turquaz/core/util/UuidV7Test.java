/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UuidV7Test {

    @Test
    void version_alanı_7_olmalı() {
        UUID id = UuidV7.next();
        assertThat(id.version()).isEqualTo(7);
        assertThat(id.variant()).isEqualTo(2);
    }

    @Test
    void zamanla_sıralı_olmalı() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        Instant t1 = t0.plusMillis(1);
        UUID a = UuidV7.at(t0);
        UUID b = UuidV7.at(t1);
        // İlk 48 bit (zaman damgası) a < b olmalı
        long aTs = a.getMostSignificantBits() >>> 16;
        long bTs = b.getMostSignificantBits() >>> 16;
        assertThat(aTs).isLessThan(bTs);
        assertThat(aTs).isEqualTo(t0.toEpochMilli());
    }

    @Test
    void benzersiz_olmalı() {
        UUID a = UuidV7.next();
        UUID b = UuidV7.next();
        assertThat(a).isNotEqualTo(b);
    }
}
