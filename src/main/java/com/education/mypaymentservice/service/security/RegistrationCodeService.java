package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.model.entity.RegistrationCode;
import com.education.mypaymentservice.repository.RegistrationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationCodeService {

    private final RegistrationCodeRepository registrationCodeRepository;

    @Value("${registration.code.expiration.hours}")
    private int codeExpirationHours;

    public String generateCode(String email) {
        String code = generateRandomCode();

        RegistrationCode registrationCode = RegistrationCode.builder()
                .code(code)
                .email(email)
                .expiresAt(LocalDateTime.now().plusHours(codeExpirationHours))
                .used(false)
                .build();

        registrationCodeRepository.save(registrationCode);

        log.info("Созданный registration-code {} и отправлен на email: {}", code, email);
        return code;
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 8);
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
}

