package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.RegistrationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, Long> {

    Optional<RegistrationCode> findByCode(String code);
    Optional<RegistrationCode> findByCodeAndEmail(String code, String email);

}
