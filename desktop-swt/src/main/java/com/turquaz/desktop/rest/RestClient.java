/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

/**
 * Türkiye'deki tek bir Turquaz API sunucusuyla konuşan minimal REST istemcisi.
 * JWT'yi taşır, Jackson ile JSON serileştirir, hata gövdesinin {@code message}
 * alanını yakalar ve {@link ApiException} olarak fırlatır.
 */
public class RestClient {

    private final ObjectMapper json;
    private final HttpClient http;
    private final String baseUrl;
    private volatile String bearerToken;

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void setBearerToken(String token) {
        this.bearerToken = token;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public <T> T get(String path, Class<T> type) {
        return parse(send("GET", path, null), type);
    }

    public <T> T get(String path, TypeReference<T> type) {
        return parse(send("GET", path, null), type);
    }

    public <T> T post(String path, Object body, Class<T> type) {
        return parse(send("POST", path, body), type);
    }

    public <T> T put(String path, Object body, Class<T> type) {
        return parse(send("PUT", path, body), type);
    }

    private HttpResponse<String> send(String method, String path, Object body) {
        try {
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json");
            if (bearerToken != null) {
                b.header("Authorization", "Bearer " + bearerToken);
            }
            HttpRequest.BodyPublisher publisher = body == null
                    ? BodyPublishers.noBody()
                    : BodyPublishers.ofString(json.writeValueAsString(body));
            if (body != null) {
                b.header("Content-Type", "application/json");
            }
            HttpRequest req = b.method(method, publisher).build();
            HttpResponse<String> res = http.send(req, BodyHandlers.ofString());
            if (res.statusCode() >= 400) {
                throw ApiException.fromBody(res.statusCode(), res.body(), json);
            }
            return res;
        } catch (IOException e) {
            throw new ApiException(0, "Ağ hatası: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "İstek yarıda kesildi", e);
        }
    }

    private <T> T parse(HttpResponse<String> res, Class<T> type) {
        if (type == Void.class || res.body() == null || res.body().isEmpty()) {
            return null;
        }
        try {
            return json.readValue(res.body(), type);
        } catch (IOException e) {
            throw new ApiException(res.statusCode(), "Yanıt çözümlenemedi: " + e.getMessage(), e);
        }
    }

    private <T> T parse(HttpResponse<String> res, TypeReference<T> type) {
        try {
            return json.readValue(res.body(), type);
        } catch (IOException e) {
            throw new ApiException(res.statusCode(), "Yanıt çözümlenemedi: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> raw(String path) {
        @SuppressWarnings("unchecked")
        Map<String, Object> m = post(path, null, Map.class);
        return m;
    }
}
