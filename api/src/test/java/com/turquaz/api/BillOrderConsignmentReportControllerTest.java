/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.bill.BillDtos.BillResponse;
import com.turquaz.api.bill.BillDtos.ConsignmentResponse;
import com.turquaz.api.bill.BillDtos.CreateBillRequest;
import com.turquaz.api.bill.BillDtos.CreateConsignmentRequest;
import com.turquaz.api.bill.BillDtos.CreateOrderRequest;
import com.turquaz.api.bill.BillDtos.OrderResponse;
import com.turquaz.api.support.AccountingEnvironment;
import com.turquaz.api.support.AuthenticatedTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class BillOrderConsignmentReportControllerTest extends AuthenticatedTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired AccountingEnvironment env;

    private UUID currentCardId;

    @BeforeEach
    void setUp() throws Exception {
        initAuth("bocr-ctrl");
        env.ensureFor(companyId);

        // Test için bir cari kart oluştur (REST API ile)
        String body = """
                {"code":"CC-BOCR-%d","name":"x","definition":"y","address":"z",
                 "taxDepartment":"VD","taxNumber":"0",
                 "creditLimit":0,"riskLimit":0,"discountRate":0,
                 "discountPayment":0,"daysToValue":0}
                """.formatted(System.nanoTime());
        MvcResult res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        JsonNode tree = json.readTree(res.getResponse().getContentAsString());
        currentCardId = UUID.fromString(tree.get("id").asText());
    }

    // -------------------- Fatura --------------------

    @Test
    void fatura_oluştur_yazdır_kapat_döngüsü() throws Exception {
        CreateBillRequest req = new CreateBillRequest(
                /*type=satış*/ 1, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10),
                "FT-API-" + System.nanoTime(), "Test satış",
                currentCardId, env.exchangeRateId, env.sequenceId);
        MvcResult res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.printed").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.open").value(true))
                .andReturn();
        BillResponse bill = json.readValue(res.getResponse().getContentAsString(), BillResponse.class);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills/" + bill.id() + "/print")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.printed").value(true));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills/" + bill.id() + "/close")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.open").value(false));

        // Çift kapatma → 400
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills/" + bill.id() + "/close")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void fatura_geçersiz_tür_400() throws Exception {
        CreateBillRequest req = new CreateBillRequest(
                /*type=99*/ 99, LocalDate.now(), LocalDate.now().plusDays(30),
                "FT-BAD-" + System.nanoTime(), "x",
                currentCardId, env.exchangeRateId, env.sequenceId);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // -------------------- Sipariş --------------------

    @Test
    void sipariş_oluştur_ve_teslim_et() throws Exception {
        // Önce bir fatura (sipariş bills_id'ye FK)
        CreateBillRequest billReq = new CreateBillRequest(
                1, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1),
                "FT-ORD-" + System.nanoTime(), "Ref fatura",
                currentCardId, env.exchangeRateId, env.sequenceId);
        MvcResult billRes = mvc.perform(MockMvcRequestBuilders.post("/api/v1/bills")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(billReq)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        BillResponse bill = json.readValue(billRes.getResponse().getContentAsString(), BillResponse.class);

        CreateOrderRequest req = new CreateOrderRequest(
                /*type*/ 1, /*docNo*/ (int)(System.nanoTime() % 100000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 3, 15),
                currentCardId, bill.id(), "Sipariş",
                10, 18,
                new BigDecimal("100"), new BigDecimal("50"),
                new BigDecimal("180"), new BigDecimal("1000"));
        MvcResult res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delivered").value(false))
                .andReturn();
        OrderResponse order = json.readValue(res.getResponse().getContentAsString(), OrderResponse.class);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/" + order.id() + "/deliver")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.delivered").value(true));

        // Çift teslim 400
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/" + order.id() + "/deliver")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // -------------------- Konsinye --------------------

    @Test
    void konsinye_oluştur_yazdır() throws Exception {
        CreateConsignmentRequest req = new CreateConsignmentRequest(
                /*type=verilen*/ 1, LocalDate.now(),
                "KNS-API-" + System.nanoTime(), "FT-REF",
                "Verilen konsinye",
                currentCardId, env.exchangeRateId, env.sequenceId);
        MvcResult res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/consignments")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.printed").value(false))
                .andReturn();
        ConsignmentResponse c = json.readValue(res.getResponse().getContentAsString(), ConsignmentResponse.class);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/consignments/" + c.id() + "/print")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.printed").value(true));
    }

    // -------------------- Rapor --------------------

    @Test
    void stok_kar_raporu_endpoint_döner() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/reports/inventory-profit")
                        .header("Authorization", authHeader())
                        .param("from", "2026-01-01T00:00:00Z")
                        .param("to", "2026-12-31T23:59:59Z"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}
