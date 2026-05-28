/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.auth;

import com.turquaz.api.auth.AuthDtos.CurrentUserResponse;
import com.turquaz.api.auth.AuthDtos.LoginRequest;
import com.turquaz.api.auth.AuthDtos.LoginResponse;
import com.turquaz.api.auth.AuthDtos.RegisterRequest;
import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.BusinessRuleException;
import com.turquaz.persistence.admin.User;
import com.turquaz.persistence.admin.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<CurrentUserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User u = authService.register(req);
        return ResponseEntity.status(201).body(new CurrentUserResponse(
                u.getId(), u.getCompanyId(), u.getUsername(), u.getUsersRealName()));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me() {
        User u = userRepository.findById(CurrentUser.userId())
                .orElseThrow(() -> new BusinessRuleException("Kullanıcı bulunamadı"));
        return ResponseEntity.ok(new CurrentUserResponse(
                u.getId(), u.getCompanyId(), u.getUsername(), u.getUsersRealName()));
    }
}
