package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.RegistrationCode;
import com.education.mypaymentservice.model.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrationCodeRepository extends JpaRepository<RegistrationCode, Long> {

    Optional<RegistrationCode> findByCode(String code);
    Optional<RegistrationCode> findByCodeAndEmail(String code, String email);
    Optional<RegistrationCode> findByEmail(String email);

    @Query("SELECT COUNT(e) FROM RegistrationCode e WHERE e.role = :role")
    int countByRole(@Param("role") Roles role);
}
