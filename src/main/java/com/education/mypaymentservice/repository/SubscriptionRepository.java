package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByActiveNot(boolean active);

    Optional<Subscription> findByClientId(UUID clientId);
}
