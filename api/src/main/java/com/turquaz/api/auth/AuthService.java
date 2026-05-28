/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.auth;

import com.turquaz.api.auth.AuthDtos.LoginRequest;
import com.turquaz.api.auth.AuthDtos.LoginResponse;
import com.turquaz.api.auth.AuthDtos.RegisterRequest;
import com.turquaz.api.security.TokenIssuer;
import com.turquaz.core.BusinessRuleException;
import com.turquaz.persistence.admin.User;
import com.turquaz.persistence.admin.UserRepository;
import com.turquaz.persistence.common.CompanyRepository;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final CompanyRepository companies;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;
    private final Duration ttl;

    public AuthService(
            UserRepository users,
            CompanyRepository companies,
            PasswordEncoder passwordEncoder,
            TokenIssuer tokenIssuer,
            @Value("${turquaz.security.jwt-ttl:PT8H}") Duration ttl) {
        this.users = users;
        this.companies = companies;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
        this.ttl = ttl;
    }

    @Transactional
    public User register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new BusinessRuleException(
                    "Bu kullanıcı adı zaten kayıtlı: " + req.username());
        }
        companies.findById(req.companyId())
                .orElseThrow(() -> new BusinessRuleException(
                        "Şirket bulunamadı: " + req.companyId()));

        User u = new User();
        u.setCompanyId(req.companyId());
        u.setUsername(req.username());
        u.setUsersPassword(passwordEncoder.encode(req.password()));
        u.setUsersRealName(req.realName());
        u.setUsersDescription("");
        u.setCreatedBy(req.username());
        u.setUpdatedBy(req.username());
        return users.save(u);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User u = users.findByUsername(req.username())
                .orElseThrow(() -> new BusinessRuleException(
                        "Kullanıcı adı veya parola hatalı"));
        if (!passwordEncoder.matches(req.password(), u.getUsersPassword())) {
            throw new BusinessRuleException("Kullanıcı adı veya parola hatalı");
        }
        String token = tokenIssuer.issue(u);
        return new LoginResponse(
                token, "Bearer", ttl.toSeconds(),
                u.getId(), u.getCompanyId(), u.getUsername());
    }
}
