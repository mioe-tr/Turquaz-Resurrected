/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.current.CurrentCardDtos.CreateCurrentCardRequest;
import com.turquaz.api.current.CurrentCardDtos.CurrentCardResponse;
import com.turquaz.api.current.CurrentCardDtos.UpdateLimitsRequest;
import com.turquaz.api.support.AuthenticatedTest;
import java.math.BigDecimal;
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
class CurrentCardControllerTest extends AuthenticatedTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @BeforeEach
    void setUp() {
        initAuth("cc-ctrl");
    }

    @Test
    void token_yokken_401() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/current-cards"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void oluştur_kodla_bul_listele_limit_güncelle() throws Exception {
        String code = "CTL-" + System.nanoTime();
        CreateCurrentCardRequest req = new CreateCurrentCardRequest(
                code, "Test Müşteri", "Tanım", "Adres",
                "Beşiktaş VD", "1234567890",
                new BigDecimal("10000"), new BigDecimal("5000"),
                new BigDecimal("0.05"), new BigDecimal("100"),
                30);

        MvcResult created = mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(code))
                .andExpect(MockMvcResultMatchers.jsonPath("$.companyId").value(companyId.toString()))
                .andReturn();

        CurrentCardResponse body = json.readValue(
                created.getResponse().getContentAsString(), CurrentCardResponse.class);

        // Kodla bul
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/current-cards/by-code/" + code)
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(body.id().toString()));

        // Listede var
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/current-cards")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.code=='" + code + "')]")
                        .exists());

        // Limit güncelle
        UpdateLimitsRequest upd = new UpdateLimitsRequest(new BigDecimal("99999"), new BigDecimal("50000"));
        mvc.perform(MockMvcRequestBuilders.put("/api/v1/current-cards/" + body.id() + "/limits")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(upd)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.creditLimit").value(99999.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.riskLimit").value(50000.0));
    }

    @Test
    void aynı_kod_iki_kez_400() throws Exception {
        String code = "DUP-CTL-" + System.nanoTime();
        CreateCurrentCardRequest req = new CreateCurrentCardRequest(
                code, "x", "y", "z", "VD", "0",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("zaten kayıtlı")));
    }

    @Test
    void bulunmayan_kod_400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/current-cards/by-code/HIC-YOK")
                        .header("Authorization", authHeader()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void doğrulama_eksik_alan_400() throws Exception {
        // Boş kod + negatif limit
        String body = """
                {"code":"","name":"","definition":"","address":"","taxDepartment":"",
                 "taxNumber":"","creditLimit":-1,"riskLimit":-1,"discountRate":-1,
                 "discountPayment":-1,"daysToValue":-1}
                """;
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/current-cards")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isArray());
    }
}
