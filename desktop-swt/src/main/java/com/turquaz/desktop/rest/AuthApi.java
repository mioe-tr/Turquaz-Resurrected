/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

public class AuthApi {

    private final RestClient client;

    public AuthApi(RestClient client) {
        this.client = client;
    }

    public LoginResponse login(String username, String password) {
        LoginResponse res = client.post("/auth/login",
                Map.of("username", username, "password", password),
                LoginResponse.class);
        client.setBearerToken(res.accessToken);
        return res;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginResponse {
        public String accessToken;
        public String tokenType;
        public long expiresInSeconds;
        public String userId;
        public String companyId;
        public String username;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentUserResponse {
        public String userId;
        public String companyId;
        public String username;
        public String realName;
    }
}
