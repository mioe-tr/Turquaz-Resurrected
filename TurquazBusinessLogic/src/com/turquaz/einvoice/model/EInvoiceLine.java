package com.turquaz.einvoice.model;

import java.math.BigDecimal;

/**
 * Fatura satýrý. KDV bilgisi Turquaz {@code TurqInventoryTransaction}
 * (vatRate / vatAmount) kayýtlarýndan gelir.
 */
public class EInvoiceLine {

    private String name;                 // ürün/hizmet adý
    private String unit = "C62";         // UBL birim kodu (C62 = adet)
    private BigDecimal quantity = BigDecimal.ONE;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal lineTotal = BigDecimal.ZERO;   // KDV hariç satýr tutarý
    private BigDecimal vatRate = BigDecimal.ZERO;     // %20 -> 20
    private BigDecimal vatAmount = BigDecimal.ZERO;

    public EInvoiceLine() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }
}
