package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_codes")
public class RegistrationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String code;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Setter
    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;

}
