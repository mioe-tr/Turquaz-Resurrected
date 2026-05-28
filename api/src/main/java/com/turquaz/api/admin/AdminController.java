/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.admin;

import com.turquaz.api.security.CurrentUser;
import com.turquaz.core.BusinessRuleException;
import com.turquaz.persistence.admin.User;
import com.turquaz.persistence.admin.UserRepository;
import com.turquaz.persistence.common.Company;
import com.turquaz.persistence.common.CompanyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Yönetim ve ayarlar uç noktaları:
 *   - kullanıcılar (şirket bazlı liste, parola değiştir)
 *   - şirket bilgisi (oku/güncelle)
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserRepository users;
    private final CompanyRepository companies;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            UserRepository users,
            CompanyRepository companies,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.companies = companies;
        this.passwordEncoder = passwordEncoder;
    }

    // ----- Kullanıcılar -----

    public record UserSummary(UUID id, String username, String realName, String description) {
        static UserSummary from(User u) {
            return new UserSummary(u.getId(), u.getUsername(),
                    u.getUsersRealName(), u.getUsersDescription());
        }
    }

    @GetMapping("/users")
    public List<UserSummary> listUsers() {
        UUID companyId = CurrentUser.companyId();
        return users.findAll().stream()
                .filter(u -> companyId.equals(u.getCompanyId()))
                .map(UserSummary::from)
                .toList();
    }

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, max = 100) String newPassword) {
    }

    @PutMapping("/users/me/password")
    @Transactional
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        User me = users.findById(CurrentUser.userId())
                .orElseThrow(() -> new BusinessRuleException("Kullanıcı bulunamadı"));
        if (!passwordEncoder.matches(req.currentPassword(), me.getUsersPassword())) {
            throw new BusinessRuleException("Mevcut parola hatalı");
        }
        me.setUsersPassword(passwordEncoder.encode(req.newPassword()));
        me.setUpdatedBy(me.getUsername());
        me.setLastModified(Instant.now());
        users.save(me);
        return ResponseEntity.noContent().build();
    }

    // ----- Şirket bilgisi -----

    public record CompanyInfo(
            UUID id, String name, String address, String telephone, String fax) {
        static CompanyInfo from(Company c) {
            return new CompanyInfo(c.getId(), c.getCompanyName(),
                    c.getCompanyAddress(), c.getCompanyTelephone(), c.getCompanyFax());
        }
    }

    public record UpdateCompanyRequest(
            @NotBlank @Size(max = 250) String name,
            @Size(max = 250) String address,
            @Size(max = 100) String telephone,
            @Size(max = 100) String fax) {
    }

    @GetMapping("/company")
    public CompanyInfo myCompany() {
        Company c = companies.findById(CurrentUser.companyId())
                .orElseThrow(() -> new BusinessRuleException("Şirket bulunamadı"));
        return CompanyInfo.from(c);
    }

    @PutMapping("/company")
    @Transactional
    public CompanyInfo updateCompany(@Valid @RequestBody UpdateCompanyRequest req) {
        Company c = companies.findById(CurrentUser.companyId())
                .orElseThrow(() -> new BusinessRuleException("Şirket bulunamadı"));
        c.setCompanyName(req.name());
        c.setCompanyAddress(req.address() == null ? "" : req.address());
        c.setCompanyTelephone(req.telephone() == null ? "" : req.telephone());
        c.setCompanyFax(req.fax() == null ? "" : req.fax());
        c.setUpdatedBy(CurrentUser.username());
        c.setLastModified(Instant.now());
        return CompanyInfo.from(companies.save(c));
    }

    // POST sözleşmesi ile aynı sürüm — istemci PUT desteklemiyorsa
    @PostMapping("/company")
    public CompanyInfo updateCompanyPost(@Valid @RequestBody UpdateCompanyRequest req) {
        return updateCompany(req);
    }
}
