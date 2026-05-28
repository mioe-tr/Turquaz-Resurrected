/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turquaz.api.auth.AuthDtos.LoginRequest;
import com.turquaz.api.auth.AuthDtos.LoginResponse;
import com.turquaz.api.auth.AuthDtos.RegisterRequest;
import com.turquaz.persistence.admin.UserRepository;
import com.turquaz.persistence.common.CompanyRepository;
import java.util.UUID;
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
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired CompanyRepository companies;
    @Autowired UserRepository users;

    @Test
    void korumalı_uç_token_yokken_401_döner() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void kayıt_login_ve_me_uçtan_uca_çalışır() throws Exception {
        UUID companyId = companies.findAll().getFirst().getId();
        String username = "user-" + System.nanoTime();
        String password = "SikiSifre2026!";

        // Kayıt
        RegisterRequest reg = new RegisterRequest(username, password, "Test Kullanıcı", companyId);
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reg)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.companyId").value(companyId.toString()));

        // Login
        LoginRequest login = new LoginRequest(username, password);
        MvcResult loginRes = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(login)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        LoginResponse loginBody = json.readValue(
                loginRes.getResponse().getContentAsString(), LoginResponse.class);
        assertThat(loginBody.accessToken()).isNotBlank();
        assertThat(loginBody.companyId()).isEqualTo(companyId);

        // Token ile /auth/me
        mvc.perform(MockMvcRequestBuilders.get("/auth/me")
                        .header("Authorization", "Bearer " + loginBody.accessToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.realName").value("Test Kullanıcı"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.companyId").value(companyId.toString()));
    }

    @Test
    void yanlış_parola_400_döner() throws Exception {
        UUID companyId = companies.findAll().getFirst().getId();
        String username = "u-" + System.nanoTime();
        RegisterRequest reg = new RegisterRequest(username, "GuzelParola1!", "x", companyId);
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reg)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new LoginRequest(username, "yanlis"))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("hatalı")));
    }

    @Test
    void aynı_kullanıcı_adı_iki_kez_kayıt_edilemez() throws Exception {
        UUID companyId = companies.findAll().getFirst().getId();
        String username = "dup-" + System.nanoTime();
        RegisterRequest reg = new RegisterRequest(username, "GuzelParola1!", "x", companyId);
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reg)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(reg)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("zaten kayıtlı")));
    }

    @Test
    void doğrulama_hatası_400_ve_detayları_döner() throws Exception {
        RegisterRequest bad = new RegisterRequest("", "kisa", "", null);
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(bad)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }
}
