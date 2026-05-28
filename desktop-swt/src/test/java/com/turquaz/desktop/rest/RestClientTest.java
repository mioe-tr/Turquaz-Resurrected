/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestClientTest {

    private HttpServer server;
    private RestClient client;
    private String lastAuthHeader;
    private String lastBody;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/auth/login", ex -> {
            byte[] req = ex.getRequestBody().readAllBytes();
            lastBody = new String(req, StandardCharsets.UTF_8);
            byte[] body = "{\"accessToken\":\"tok\",\"username\":\"u\"}".getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(200, body.length);
            try (OutputStream out = ex.getResponseBody()) { out.write(body); }
        });
        server.createContext("/protected", ex -> {
            lastAuthHeader = ex.getRequestHeaders().getFirst("Authorization");
            byte[] body = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(200, body.length);
            try (OutputStream out = ex.getResponseBody()) { out.write(body); }
        });
        server.createContext("/bad", ex -> {
            byte[] body = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Kayıt zaten var\"}"
                    .getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(400, body.length);
            try (OutputStream out = ex.getResponseBody()) { out.write(body); }
        });
        server.start();
        client = new RestClient("http://localhost:" + server.getAddress().getPort());
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void login_json_body_gönderir_ve_token_set_eder() {
        AuthApi auth = new AuthApi(client);
        AuthApi.LoginResponse res = auth.login("alice", "pw");
        assertThat(res.accessToken).isEqualTo("tok");
        assertThat(client.getBearerToken()).isEqualTo("tok");
        assertThat(lastBody).contains("alice").contains("pw");
    }

    @Test
    void bearer_header_otomatik_eklenir() {
        client.setBearerToken("XYZ");
        @SuppressWarnings("unchecked")
        Map<String, Object> body = client.get("/protected", Map.class);
        assertThat(body.get("ok")).isEqualTo(true);
        assertThat(lastAuthHeader).isEqualTo("Bearer XYZ");
    }

    @Test
    void hata_mesajı_message_alanından_çıkar() {
        assertThatThrownBy(() -> client.post("/bad", Map.of(), Map.class))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Kayıt zaten var")
                .matches(e -> ((ApiException) e).getStatus() == 400);
    }

    @Test
    void ApiException_düz_metin_gövdesinde_de_kullanılabilir() {
        ObjectMapper json = new ObjectMapper();
        ApiException ex = ApiException.fromBody(500, "bozuk", json);
        assertThat(ex.getStatus()).isEqualTo(500);
        assertThat(ex.getMessage()).isEqualTo("bozuk");
    }
}
