package com.education.mypaymentservice.service.repository;

import com.education.mypaymentservice.dto.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository <Client, UUID>{
    Optional<Client> findByPhone(String phone);
}
