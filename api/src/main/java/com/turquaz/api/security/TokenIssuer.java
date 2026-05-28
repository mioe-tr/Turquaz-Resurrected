/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.security;

import com.turquaz.persistence.admin.User;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * JWT (HS256) üretici. Token gövdesinde {@code sub=username}, {@code companyId},
 * {@code userId} taşır. Doğrulama Spring Security'nin OAuth2 resource server
 * filtresi tarafından yapılır.
 */
@Service
public class TokenIssuer {

    private final JwtEncoder encoder;
    private final Duration ttl;

    public TokenIssuer(
            JwtEncoder encoder,
            @Value("${turquaz.security.jwt-ttl:PT8H}") Duration ttl) {
        this.encoder = encoder;
        this.ttl = ttl;
    }

    public String issue(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("turquaz")
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("companyId", user.getCompanyId().toString())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
