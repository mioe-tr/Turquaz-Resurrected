/*
 * Turquaz Resurrected - Modern Türk muhasebe yazılımı
 * Copyright (C) 2026  Turquaz Resurrected katkıcıları
 * GPLv3 — bkz. LICENSE.
 */
package com.turquaz.api.support;

import com.turquaz.api.security.TokenIssuer;
import com.turquaz.core.BusinessRuleException;
import com.turquaz.persistence.admin.User;
import com.turquaz.persistence.admin.UserRepository;
import com.turquaz.persistence.common.CompanyRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * MockMvc tabanlı controller testleri için JWT bağlamı sağlar. Her test
 * sınıfı bu yardımcıdan kalıtım almak yerine instance compose eder.
 */
public class AuthenticatedTest {

    @Autowired protected UserRepository userRepository;
    @Autowired protected CompanyRepository companyRepository;
    @Autowired protected TokenIssuer tokenIssuer;
    @Autowired protected PasswordEncoder passwordEncoder;

    protected UUID companyId;
    protected User testUser;
    protected String bearerToken;

    /** Test başında çağır: bir şirket bul, test kullanıcısı oluştur, JWT üret. */
    protected void initAuth(String usernamePrefix) {
        companyId = companyRepository.findAll().getFirst().getId();
        String username = usernamePrefix + "-" + System.nanoTime();
        User u = new User();
        u.setCompanyId(companyId);
        u.setUsername(username);
        u.setUsersPassword(passwordEncoder.encode("test"));
        u.setUsersRealName("Test");
        u.setUsersDescription("");
        u.setCreatedBy("test");
        u.setUpdatedBy("test");
        testUser = userRepository.save(u);
        bearerToken = "Bearer " + tokenIssuer.issue(testUser);
    }

    protected String authHeader() {
        if (bearerToken == null) {
            throw new BusinessRuleException("initAuth() çağrılmamış");
        }
        return bearerToken;
    }
}
