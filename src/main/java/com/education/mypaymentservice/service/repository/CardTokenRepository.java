package com.education.mypaymentservice.service.repository;

import com.education.mypaymentservice.dto.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
}
