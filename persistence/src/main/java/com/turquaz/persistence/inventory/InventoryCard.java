/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "turq_inventory_cards")
@AttributeOverride(name = "lastModified", column = @Column(name = "update_date", nullable = false))
public class InventoryCard extends CompanyScopedEntity {

    @Column(name = "card_inventory_code", nullable = false, length = 25)
    private String cardInventoryCode;

    @Column(name = "card_name", nullable = false, length = 50)
    private String cardName;

    @Column(name = "card_definition", nullable = false, length = 50)
    private String cardDefinition;

    @Column(name = "card_minimum_amount", nullable = false)
    private Integer cardMinimumAmount;

    @Column(name = "card_maximum_amount", nullable = false)
    private Integer cardMaximumAmount;

    @Column(name = "card_vat", nullable = false)
    private Integer cardVat;

    @Column(name = "card_discount", nullable = false)
    private Integer cardDiscount;

    @Column(name = "card_special_vat", nullable = false)
    private Integer cardSpecialVat;

    @Column(name = "card_special_vat_each", nullable = false)
    private BigDecimal cardSpecialVatEach;

    @Column(name = "spec_vat_for_each", nullable = false)
    private Boolean specVatForEach;

    public String getCardInventoryCode() { return cardInventoryCode; }
    public void setCardInventoryCode(String v) { this.cardInventoryCode = v; }

    public String getCardName() { return cardName; }
    public void setCardName(String v) { this.cardName = v; }

    public String getCardDefinition() { return cardDefinition; }
    public void setCardDefinition(String v) { this.cardDefinition = v; }

    public Integer getCardMinimumAmount() { return cardMinimumAmount; }
    public void setCardMinimumAmount(Integer v) { this.cardMinimumAmount = v; }

    public Integer getCardMaximumAmount() { return cardMaximumAmount; }
    public void setCardMaximumAmount(Integer v) { this.cardMaximumAmount = v; }

    public Integer getCardVat() { return cardVat; }
    public void setCardVat(Integer v) { this.cardVat = v; }

    public Integer getCardDiscount() { return cardDiscount; }
    public void setCardDiscount(Integer v) { this.cardDiscount = v; }

    public Integer getCardSpecialVat() { return cardSpecialVat; }
    public void setCardSpecialVat(Integer v) { this.cardSpecialVat = v; }

    public BigDecimal getCardSpecialVatEach() { return cardSpecialVatEach; }
    public void setCardSpecialVatEach(BigDecimal v) { this.cardSpecialVatEach = v; }

    public Boolean getSpecVatForEach() { return specVatForEach; }
    public void setSpecVatForEach(Boolean v) { this.specVatForEach = v; }

}
