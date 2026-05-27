/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 *
 * Bu program özgür yazılımdır: GNU Genel Kamu Lisansı (GPL) sürüm 3 veya
 * (tercihinize göre) daha sonraki bir sürümün koşulları altında yeniden
 * dağıtabilir ve/veya değiştirebilirsiniz. Ayrıntılar için LICENSE dosyasına
 * ve <https://www.gnu.org/licenses/> adresine bakınız.
 */

/**
 * Turquaz çekirdek iş kuralları ve domain modeli.
 *
 * <p>Eski {@code TurquazBusinessLogic} modülü ile {@code com.turquaz.engine.bl.*}
 * paketlerinin modern karşılığı. Çift taraflı muhasebe motoru, mizan ve para
 * hesapları burada (Faz 2'de) yer alacak. Para her zaman {@link java.math.BigDecimal}.
 */
package com.turquaz.core;
