/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.core.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.turquaz.core.money.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JournalLineTest {

    private static final UUID ACCOUNT = UUID.randomUUID();

    @Test
    void borç_satırı_oluşur() {
        JournalLine line = JournalLine.debit(ACCOUNT, Money.of("100"), "test");
        assertThat(line.debit()).isEqualTo(Money.of("100"));
        assertThat(line.credit().isZero()).isTrue();
    }

    @Test
    void alacak_satırı_oluşur() {
        JournalLine line = JournalLine.credit(ACCOUNT, Money.of("100"), "test");
        assertThat(line.credit()).isEqualTo(Money.of("100"));
        assertThat(line.debit().isZero()).isTrue();
    }

    @Test
    void aynı_satırda_hem_borç_hem_alacak_reddedilir() {
        assertThatThrownBy(() -> new JournalLine(ACCOUNT, Money.of("10"), Money.of("10"), "x"))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("hem borç hem alacak");
    }

    @Test
    void iki_taraf_da_sıfır_reddedilir() {
        assertThatThrownBy(() -> new JournalLine(ACCOUNT, Money.ZERO, Money.ZERO, "x"))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("ikisi birden sıfır");
    }

    @Test
    void negatif_tutar_reddedilir() {
        assertThatThrownBy(() -> new JournalLine(ACCOUNT, Money.of("-1"), Money.ZERO, "x"))
                .isInstanceOf(AccountingException.class)
                .hasMessageContaining("negatif olamaz");
    }

    @Test
    void hesap_yoksa_reddedilir() {
        assertThatThrownBy(() -> new JournalLine(null, Money.of("10"), Money.ZERO, "x"))
                .isInstanceOf(AccountingException.class);
    }
}
