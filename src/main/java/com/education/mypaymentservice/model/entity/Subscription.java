package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType subscriptionType;

    @Column(nullable = false)
    private LocalDate startTime;

    @Column(nullable = false)
    private LocalDate endTime;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private boolean active;
}
