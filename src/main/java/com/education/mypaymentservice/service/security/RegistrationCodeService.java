package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.RegistrationCode;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.response.RegistrationCodeResponse;
import com.education.mypaymentservice.repository.RegistrationCodeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationCodeService {

    private final RegistrationCodeRepository registrationCodeRepository;
    private final Environment environment;

    @Value("${registration.code.expiration.hours}")
    private int codeExpirationHours;

    public RegistrationCodeResponse generateCode(String email) {
        String code = generateRandomCode();
        Roles role = Roles.ROLE_EMPLOYEE;

        if (email.equals(environment.getProperty("app.admin.email"))) {
            role = Roles.ROLE_ADMIN;
        }

        RegistrationCode registrationCode = RegistrationCode.builder()
                .code(code)
                .email(email)
                .expiresAt(LocalDateTime.now().plusHours(codeExpirationHours))
                .used(false)
                .role(role)
                .build();

        registrationCodeRepository.save(registrationCode);

        log.info("Созданный registration-code {} и отправлен на email: {}", code, email);
        return new RegistrationCodeResponse(code);
    }

    public RegistrationCode findRegistrationCodeByEmail(String email) {
        return registrationCodeRepository.findByEmail(email).orElseThrow(()
                -> new PaymentServiceException("Админ с email: " + email + " не найден!"));
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 20);
    }

    public boolean isValidateCode(String code, String email) {
        return registrationCodeRepository.findByCodeAndEmail(code, email)
                .filter(registrationCode -> !registrationCode.isUsed())
                .filter(registrationCode -> registrationCode.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void changeCodeAsUsed(String code) {
        registrationCodeRepository.findByCode(code)
                .ifPresent(registrationCode -> {
                    registrationCode.setUsed(true);
                    registrationCodeRepository.save(registrationCode);
                });
    }

    @PostConstruct
    public void initialize() {
        if (registrationCodeRepository.countByRole(Roles.ROLE_ADMIN) == 0) {
            String clientEmail = environment.getProperty("app.admin.email");

            assert clientEmail != null;
            generateCode(clientEmail);
        }
    }

    public boolean validateCode(String code) {
        RegistrationCode registrationCodeAdmin = findRegistrationCodeByEmail(
                environment.getProperty("app.admin.email")
        );

        boolean isValid = code.equals(registrationCodeAdmin.getCode());
        if (isValid) {
            changeCodeAsUsed(code);
        }
        return isValid;
    }
}

