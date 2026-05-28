/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 *
 * Bu program özgür yazılımdır: GNU Genel Kamu Lisansı (GPL) sürüm 3 veya
 * (tercihinize göre) daha sonraki bir sürümün koşulları altında yeniden
 * dağıtabilir ve/veya değiştirebilirsiniz.
 *
 * Bu program, faydalı olması ümidiyle dağıtılmaktadır; ancak HİÇBİR
 * GARANTİSİ YOKTUR; SATILABİLİRLİK veya BELİRLİ BİR AMACA UYGUNLUK zımni
 * garantisi dahi verilmez. Ayrıntılar için GNU Genel Kamu Lisansı'na bakınız.
 *
 * Bu programla birlikte GNU Genel Kamu Lisansı'nın bir kopyasını almış
 * olmalısınız. Almadıysanız <https://www.gnu.org/licenses/> adresine bakınız.
 */
package com.turquaz.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.turquaz")
@EnableJpaRepositories(basePackages = "com.turquaz.persistence")
@EntityScan(basePackages = "com.turquaz.persistence")
public class TurquazApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurquazApplication.class, args);
    }
}
