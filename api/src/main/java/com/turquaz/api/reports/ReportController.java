/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.reports;

import com.turquaz.api.security.CurrentUser;
import com.turquaz.persistence.inventory.CardProfit;
import com.turquaz.persistence.inventory.InventoryProfitService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final InventoryProfitService profitService;

    public ReportController(InventoryProfitService profitService) {
        this.profitService = profitService;
    }

    public record InventoryProfitRow(
            UUID cardId,
            BigDecimal amountIn, BigDecimal amountOut,
            BigDecimal costIn, BigDecimal revenueOut,
            BigDecimal avgUnitCost, BigDecimal costOfSold, BigDecimal profit) {

        static InventoryProfitRow from(CardProfit p) {
            return new InventoryProfitRow(
                    p.cardId(),
                    p.amountIn().value(), p.amountOut().value(),
                    p.costIn().amount(), p.revenueOut().amount(),
                    p.avgUnitCost().amount(), p.costOfSold().amount(),
                    p.profit().amount());
        }
    }

    /**
     * Stok kâr analizi: belirli bir dönemde her stok kartı için ortalama
     * maliyet yöntemiyle hesaplanmış kâr/maliyet/gelir.
     */
    @GetMapping("/inventory-profit")
    public List<InventoryProfitRow> inventoryProfit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return profitService.report(CurrentUser.companyId(), from, to).stream()
                .map(InventoryProfitRow::from).toList();
    }
}
