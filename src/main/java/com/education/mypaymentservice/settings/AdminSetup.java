package com.education.mypaymentservice.settings;

import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdminSetup {

    private final EmployeeRepository employeeRepository;
    private final Environment environment;

    private final CacheManager cacheManager;
    private static final String SETUP_TOKEN_CACHE = "setupTokenCache";

    @PostConstruct
    public void initialize() {
        if (employeeRepository.countByRole(Roles.ROLE_ADMIN) == 0) {
            String setupToken = generateSetupToken();
            Objects.requireNonNull(cacheManager.getCache(SETUP_TOKEN_CACHE)).put("adminSetupToken", setupToken);

            String clientEmail = environment.getProperty("app.admin.email");

            log.info("Отправлен Setup-Token: {} на email: {}", setupToken, clientEmail);
        }
    }

    private String generateSetupToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isFirstAdminSetupComplete() {
        return employeeRepository.countByRole(Roles.ROLE_ADMIN) > 0;
    }

    public boolean validateSetupToken(String token) {
        Cache.ValueWrapper storedToken = Objects.requireNonNull(cacheManager.getCache(SETUP_TOKEN_CACHE))
                .get("adminSetupToken");
        return storedToken != null && token.equals(storedToken.get());
    }

    public void invalidateSetupToken() {
        Objects.requireNonNull(cacheManager.getCache(SETUP_TOKEN_CACHE)).evict("adminSetupToken");
    }
}

