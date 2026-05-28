/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.inventory;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_inventory_transactions")
public class InventoryTransaction extends CompanyScopedEntity {

    @Column(name = "inventory_cards_id", nullable = false)
    private UUID inventoryCardsId;

    @Column(name = "inventory_warehouses_id", nullable = false)
    private UUID inventoryWarehousesId;

    @Column(name = "engine_sequences_id", nullable = false)
    private UUID engineSequencesId;

    @Column(name = "amount_in", nullable = false)
    private BigDecimal amountIn;

    @Column(name = "inventory_units_id", nullable = false)
    private UUID inventoryUnitsId;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "discount_rate", nullable = false)
    private BigDecimal discountRate;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;

    @Column(name = "vat_special_unit_price", nullable = false)
    private BigDecimal vatSpecialUnitPrice;

    @Column(name = "vat_special_rate", nullable = false)
    private BigDecimal vatSpecialRate;

    @Column(name = "vat_special_amount", nullable = false)
    private BigDecimal vatSpecialAmount;

    @Column(name = "cumilative_price", nullable = false)
    private BigDecimal cumilativePrice;

    @Column(name = "amount_out", nullable = false)
    private BigDecimal amountOut;

    @Column(name = "transactions_date", nullable = false)
    private Instant transactionsDate;

    @Column(name = "transaction_type", nullable = false)
    private UUID transactionType;

    @Column(name = "document_no", nullable = false, length = 100)
    private String documentNo;

    @Column(name = "definition", nullable = false, length = 250)
    private String definition;

    @Column(name = "exchange_rate_id", nullable = false)
    private UUID exchangeRateId;

    @Column(name = "vat_rate", nullable = false)
    private BigDecimal vatRate;

    @Column(name = "unit_price_in_foreign_currency", nullable = false)
    private BigDecimal unitPriceInForeignCurrency;

    @Column(name = "total_price_in_foreign_currency", nullable = false)
    private BigDecimal totalPriceInForeignCurrency;

    @Column(name = "discount_amount_in_foreign_currency", nullable = false)
    private BigDecimal discountAmountInForeignCurrency;

    @Column(name = "vat_amount_in_foreign_currency", nullable = false)
    private BigDecimal vatAmountInForeignCurrency;

    @Column(name = "vat_special_unit_price_in_foreign_currency", nullable = false)
    private BigDecimal vatSpecialUnitPriceInForeignCurrency;

    @Column(name = "vat_special_amount_in_foreign_currency", nullable = false)
    private BigDecimal vatSpecialAmountInForeignCurrency;

    @Column(name = "cumilative_price_in_foreign_currency", nullable = false)
    private BigDecimal cumilativePriceInForeignCurrency;

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    public UUID getInventoryCardsId() { return inventoryCardsId; }
    public void setInventoryCardsId(UUID v) { this.inventoryCardsId = v; }

    public UUID getInventoryWarehousesId() { return inventoryWarehousesId; }
    public void setInventoryWarehousesId(UUID v) { this.inventoryWarehousesId = v; }

    public UUID getEngineSequencesId() { return engineSequencesId; }
    public void setEngineSequencesId(UUID v) { this.engineSequencesId = v; }

    public BigDecimal getAmountIn() { return amountIn; }
    public void setAmountIn(BigDecimal v) { this.amountIn = v; }

    public UUID getInventoryUnitsId() { return inventoryUnitsId; }
    public void setInventoryUnitsId(UUID v) { this.inventoryUnitsId = v; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal v) { this.totalPrice = v; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal v) { this.discountRate = v; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal v) { this.discountAmount = v; }

    public BigDecimal getVatAmount() { return vatAmount; }
    public void setVatAmount(BigDecimal v) { this.vatAmount = v; }

    public BigDecimal getVatSpecialUnitPrice() { return vatSpecialUnitPrice; }
    public void setVatSpecialUnitPrice(BigDecimal v) { this.vatSpecialUnitPrice = v; }

    public BigDecimal getVatSpecialRate() { return vatSpecialRate; }
    public void setVatSpecialRate(BigDecimal v) { this.vatSpecialRate = v; }

    public BigDecimal getVatSpecialAmount() { return vatSpecialAmount; }
    public void setVatSpecialAmount(BigDecimal v) { this.vatSpecialAmount = v; }

    public BigDecimal getCumilativePrice() { return cumilativePrice; }
    public void setCumilativePrice(BigDecimal v) { this.cumilativePrice = v; }

    public BigDecimal getAmountOut() { return amountOut; }
    public void setAmountOut(BigDecimal v) { this.amountOut = v; }

    public Instant getTransactionsDate() { return transactionsDate; }
    public void setTransactionsDate(Instant v) { this.transactionsDate = v; }

    public UUID getTransactionType() { return transactionType; }
    public void setTransactionType(UUID v) { this.transactionType = v; }

    public String getDocumentNo() { return documentNo; }
    public void setDocumentNo(String v) { this.documentNo = v; }

    public String getDefinition() { return definition; }
    public void setDefinition(String v) { this.definition = v; }

    public UUID getExchangeRateId() { return exchangeRateId; }
    public void setExchangeRateId(UUID v) { this.exchangeRateId = v; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal v) { this.vatRate = v; }

    public BigDecimal getUnitPriceInForeignCurrency() { return unitPriceInForeignCurrency; }
    public void setUnitPriceInForeignCurrency(BigDecimal v) { this.unitPriceInForeignCurrency = v; }

    public BigDecimal getTotalPriceInForeignCurrency() { return totalPriceInForeignCurrency; }
    public void setTotalPriceInForeignCurrency(BigDecimal v) { this.totalPriceInForeignCurrency = v; }

    public BigDecimal getDiscountAmountInForeignCurrency() { return discountAmountInForeignCurrency; }
    public void setDiscountAmountInForeignCurrency(BigDecimal v) { this.discountAmountInForeignCurrency = v; }

    public BigDecimal getVatAmountInForeignCurrency() { return vatAmountInForeignCurrency; }
    public void setVatAmountInForeignCurrency(BigDecimal v) { this.vatAmountInForeignCurrency = v; }

    public BigDecimal getVatSpecialUnitPriceInForeignCurrency() { return vatSpecialUnitPriceInForeignCurrency; }
    public void setVatSpecialUnitPriceInForeignCurrency(BigDecimal v) { this.vatSpecialUnitPriceInForeignCurrency = v; }

    public BigDecimal getVatSpecialAmountInForeignCurrency() { return vatSpecialAmountInForeignCurrency; }
    public void setVatSpecialAmountInForeignCurrency(BigDecimal v) { this.vatSpecialAmountInForeignCurrency = v; }

    public BigDecimal getCumilativePriceInForeignCurrency() { return cumilativePriceInForeignCurrency; }
    public void setCumilativePriceInForeignCurrency(BigDecimal v) { this.cumilativePriceInForeignCurrency = v; }

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

}
