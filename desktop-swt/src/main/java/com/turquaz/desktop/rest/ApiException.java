/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.desktop.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** REST API'den dönen 4xx/5xx hatalarını sarar. */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public ApiException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    /** Spring {@code ApiExceptionHandler.ErrorResponse} gövdesini parse eder. */
    public static ApiException fromBody(int status, String body, ObjectMapper json) {
        if (body == null || body.isEmpty()) {
            return new ApiException(status, "HTTP " + status);
        }
        try {
            JsonNode node = json.readTree(body);
            JsonNode msg = node.get("message");
            if (msg != null && msg.isTextual()) {
                return new ApiException(status, msg.asText());
            }
            return new ApiException(status, body);
        } catch (Exception e) {
            return new ApiException(status, body);
        }
    }
}
