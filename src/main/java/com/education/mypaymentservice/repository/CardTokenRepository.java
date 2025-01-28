package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
}
