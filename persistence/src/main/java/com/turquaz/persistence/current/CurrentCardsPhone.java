/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.current;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "turq_current_cards_phones")
public class CurrentCardsPhone extends CompanyScopedEntity {

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "phones_country_code", nullable = false)
    private Integer phonesCountryCode;

    @Column(name = "phones_city_code", nullable = false)
    private Integer phonesCityCode;

    @Column(name = "phones_number", nullable = false)
    private Integer phonesNumber;

    @Column(name = "phones_type", nullable = false, length = 50)
    private String phonesType;

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public Integer getPhonesCountryCode() { return phonesCountryCode; }
    public void setPhonesCountryCode(Integer v) { this.phonesCountryCode = v; }

    public Integer getPhonesCityCode() { return phonesCityCode; }
    public void setPhonesCityCode(Integer v) { this.phonesCityCode = v; }

    public Integer getPhonesNumber() { return phonesNumber; }
    public void setPhonesNumber(Integer v) { this.phonesNumber = v; }

    public String getPhonesType() { return phonesType; }
    public void setPhonesType(String v) { this.phonesType = v; }

}
