/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {
    }

    public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds,
            UUID userId,
            UUID companyId,
            String username) {
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 30) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(max = 250) String realName,
            @NotNull UUID companyId) {
    }

    public record CurrentUserResponse(
            UUID userId,
            UUID companyId,
            String username,
            String realName) {
    }
}
