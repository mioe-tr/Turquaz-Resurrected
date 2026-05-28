/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.persistence.bill;

import com.turquaz.core.BusinessRuleException;
import com.turquaz.core.money.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sipariş servisi. Eski sipariş BL'lerinin modern karşılığı. Sipariş henüz
 * fatura değil — yevmiyeye doğrudan postlanmaz; faturalandığında etki edecek.
 */
@Service
public class OrderService {

    public static final int TYPE_SALES = 1;
    public static final int TYPE_PURCHASE = 2;

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Order create(NewOrder cmd) {
        if (cmd.type() != TYPE_SALES && cmd.type() != TYPE_PURCHASE) {
            throw new BusinessRuleException(
                    "Geçersiz sipariş türü: " + cmd.type() + " (1=satış, 2=alış)");
        }
        if (cmd.dueDate().isBefore(cmd.orderDate())) {
            throw new BusinessRuleException("Vade tarihi sipariş tarihinden önce olamaz");
        }
        if (cmd.deliverDate().isBefore(cmd.orderDate())) {
            throw new BusinessRuleException("Teslim tarihi sipariş tarihinden önce olamaz");
        }
        Order o = new Order();
        o.setCompanyId(cmd.companyId());
        o.setOrdersType(cmd.type());
        o.setOrdersDate(cmd.orderDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        o.setOrdersDueDate(cmd.dueDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        o.setOrdersDeliverDate(cmd.deliverDate().atStartOfDay(ZoneOffset.UTC).toInstant());
        o.setCurrentCardsId(cmd.currentCardId());
        o.setOrdersDefinition(cmd.definition());
        o.setOrdersDocumentNo(cmd.documentNo());
        o.setBillsId(cmd.billId());
        o.setOrdersDiscountRate(cmd.discountRatePercent());
        o.setOrdersVat(cmd.vatPercent());
        o.setOrdersDiscountAmount(cmd.discountAmount().amount());
        o.setOrdersCharges(cmd.charges().amount());
        o.setOrdersVatAmount(cmd.vatAmount().amount());
        o.setOrdersTotalAmount(cmd.totalAmount().amount());
        o.setOrdersDelivered(0); // 0=teslim edilmedi, 1=teslim edildi
        o.setCreatedBy(cmd.currentUser());
        o.setUpdatedBy(cmd.currentUser());
        return repo.save(o);
    }

    @Transactional
    public Order markDelivered(UUID orderId, String currentUser) {
        Order o = repo.findById(orderId)
                .orElseThrow(() -> new BusinessRuleException("Sipariş bulunamadı: " + orderId));
        if (o.getOrdersDelivered() != null && o.getOrdersDelivered() == 1) {
            throw new BusinessRuleException(
                    "Sipariş zaten teslim edildi olarak işaretlenmiş: " + o.getOrdersDocumentNo());
        }
        o.setOrdersDelivered(1);
        o.setUpdatedBy(currentUser);
        o.setLastModified(java.time.Instant.now());
        return repo.save(o);
    }

    @Transactional(readOnly = true)
    public List<Order> listByCurrentCard(UUID companyId, UUID currentCardId) {
        return repo.findByCompanyIdAndCurrentCardsId(companyId, currentCardId);
    }
}
