/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.persistence.CompanyScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "turq_orders")
public class Order extends CompanyScopedEntity {

    @Column(name = "orders_document_no", nullable = false)
    private Integer ordersDocumentNo;

    @Column(name = "bills_id", nullable = false)
    private UUID billsId;

    @Column(name = "orders_date", nullable = false)
    private Instant ordersDate;

    @Column(name = "current_cards_id", nullable = false)
    private UUID currentCardsId;

    @Column(name = "orders_definition", nullable = false, length = 250)
    private String ordersDefinition;

    @Column(name = "orders_discount_rate", nullable = false)
    private Integer ordersDiscountRate;

    @Column(name = "orders_vat", nullable = false)
    private Integer ordersVat;

    @Column(name = "orders_discount_amount", nullable = false)
    private BigDecimal ordersDiscountAmount;

    @Column(name = "orders_charges", nullable = false)
    private BigDecimal ordersCharges;

    @Column(name = "orders_vat_amount", nullable = false)
    private BigDecimal ordersVatAmount;

    @Column(name = "orders_total_amount", nullable = false)
    private BigDecimal ordersTotalAmount;

    @Column(name = "orders_due_date", nullable = false)
    private Instant ordersDueDate;

    @Column(name = "orders_deliver_date", nullable = false)
    private Instant ordersDeliverDate;

    @Column(name = "orders_delivered", nullable = false)
    private Integer ordersDelivered;

    @Column(name = "orders_type", nullable = false)
    private Integer ordersType;

    public Integer getOrdersDocumentNo() { return ordersDocumentNo; }
    public void setOrdersDocumentNo(Integer v) { this.ordersDocumentNo = v; }

    public UUID getBillsId() { return billsId; }
    public void setBillsId(UUID v) { this.billsId = v; }

    public Instant getOrdersDate() { return ordersDate; }
    public void setOrdersDate(Instant v) { this.ordersDate = v; }

    public UUID getCurrentCardsId() { return currentCardsId; }
    public void setCurrentCardsId(UUID v) { this.currentCardsId = v; }

    public String getOrdersDefinition() { return ordersDefinition; }
    public void setOrdersDefinition(String v) { this.ordersDefinition = v; }

    public Integer getOrdersDiscountRate() { return ordersDiscountRate; }
    public void setOrdersDiscountRate(Integer v) { this.ordersDiscountRate = v; }

    public Integer getOrdersVat() { return ordersVat; }
    public void setOrdersVat(Integer v) { this.ordersVat = v; }

    public BigDecimal getOrdersDiscountAmount() { return ordersDiscountAmount; }
    public void setOrdersDiscountAmount(BigDecimal v) { this.ordersDiscountAmount = v; }

    public BigDecimal getOrdersCharges() { return ordersCharges; }
    public void setOrdersCharges(BigDecimal v) { this.ordersCharges = v; }

    public BigDecimal getOrdersVatAmount() { return ordersVatAmount; }
    public void setOrdersVatAmount(BigDecimal v) { this.ordersVatAmount = v; }

    public BigDecimal getOrdersTotalAmount() { return ordersTotalAmount; }
    public void setOrdersTotalAmount(BigDecimal v) { this.ordersTotalAmount = v; }

    public Instant getOrdersDueDate() { return ordersDueDate; }
    public void setOrdersDueDate(Instant v) { this.ordersDueDate = v; }

    public Instant getOrdersDeliverDate() { return ordersDeliverDate; }
    public void setOrdersDeliverDate(Instant v) { this.ordersDeliverDate = v; }

    public Integer getOrdersDelivered() { return ordersDelivered; }
    public void setOrdersDelivered(Integer v) { this.ordersDelivered = v; }

    public Integer getOrdersType() { return ordersType; }
    public void setOrdersType(Integer v) { this.ordersType = v; }

}
