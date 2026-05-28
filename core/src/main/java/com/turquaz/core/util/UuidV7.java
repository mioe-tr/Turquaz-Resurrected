/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 *
 * Bu program özgür yazılımdır: GNU Genel Kamu Lisansı (GPL) sürüm 3 veya
 * (tercihinize göre) daha sonraki bir sürümün koşulları altında dağıtabilir
 * ve/veya değiştirebilirsiniz. Ayrıntılar için LICENSE dosyasına bakınız.
 */
package com.turquaz.core.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * UUID sürüm 7 üretici (RFC 9562).
 *
 * <p>İlk 48 bit = Unix epoch milisaniye → zaman-sıralı; B-tree birincil anahtar
 * yerleşimi UUIDv4'e kıyasla çok daha iyi. Kalan bitler şifresel rastgelelik.
 *
 * <p>İş parçacığı güvenli: dahili {@link SecureRandom} senkronize edilmiştir.
 */
public final class UuidV7 {

    private static final SecureRandom RNG = new SecureRandom();

    private UuidV7() {}

    public static UUID next() {
        return at(Instant.now());
    }

    public static UUID at(Instant instant) {
        long unixMillis = instant.toEpochMilli();
        byte[] randomBytes = new byte[10];
        synchronized (RNG) {
            RNG.nextBytes(randomBytes);
        }

        long msb =
                ((unixMillis & 0xFFFFFFFFFFFFL) << 16)
                        | (0x7L << 12)
                        | ((randomBytes[0] & 0x0FL) << 8)
                        | (randomBytes[1] & 0xFFL);

        long lsb = (0x2L << 62);
        lsb |= ((long) (randomBytes[2] & 0x3F)) << 56;
        for (int i = 3; i < 10; i++) {
            lsb |= ((long) (randomBytes[i] & 0xFF)) << ((9 - i) * 8);
        }

        return new UUID(msb, lsb);
    }
}
