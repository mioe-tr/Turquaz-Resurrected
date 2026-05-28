/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.inventory.InventoryDtos.CreateInventoryCardRequest;
import com.turquaz.api.inventory.InventoryDtos.CreateWarehouseRequest;
import com.turquaz.api.inventory.InventoryDtos.InventoryCardResponse;
import com.turquaz.api.inventory.InventoryDtos.WarehouseResponse;
import com.turquaz.api.support.AuthenticatedTest;
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
class InventoryControllerTest extends AuthenticatedTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @BeforeEach
    void setUp() {
        initAuth("inv-ctrl");
    }

    @Test
    void stok_kartı_oluştur_ve_listele() throws Exception {
        String code = "INV-" + System.nanoTime();
        CreateInventoryCardRequest req = new CreateInventoryCardRequest(
                code, "Test Ürün", "Tanım", 10, 100, 18, 0, 0);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(code));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/cards/by-code/" + code)
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vatRate").value(18));
    }

    @Test
    void depo_oluştur_ve_listele() throws Exception {
        String code = "WH-" + System.nanoTime();
        CreateWarehouseRequest req = new CreateWarehouseRequest(
                code, "Ana Depo", "Adres", "İstanbul", "0212", "Açıklama");
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/warehouses")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(code));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/warehouses")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void stok_bakiyesi_endpoint_döner() throws Exception {
        // Önce kart ve depo oluştur (transactions yok → bakiye 0 olmalı)
        String invCode = "B-INV-" + System.nanoTime();
        String whCode = "B-WH-" + System.nanoTime();

        MvcResult inv = mvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new CreateInventoryCardRequest(
                                invCode, "x", "y", 0, 1000, 18, 0, 0))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        MvcResult wh = mvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/warehouses")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new CreateWarehouseRequest(
                                whCode, "X", "y", "z", "0", "w"))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        var invBody = json.readValue(inv.getResponse().getContentAsString(), InventoryCardResponse.class);
        var whBody = json.readValue(wh.getResponse().getContentAsString(), WarehouseResponse.class);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/ledger/stock")
                        .header("Authorization", authHeader())
                        .param("cardId", invBody.id().toString())
                        .param("warehouseId", whBody.id().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(0));
    }

    @Test
    void min_maks_tutarsızlığı_400() throws Exception {
        CreateInventoryCardRequest req = new CreateInventoryCardRequest(
                "BAD-" + System.nanoTime(), "x", "y",
                /*min*/ 50, /*max*/ 10, 18, 0, 0);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Minimum miktar")));
    }
}
