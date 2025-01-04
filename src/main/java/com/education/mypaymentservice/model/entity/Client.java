package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clients")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String surname;

    @Column(length = 50)
    private String midname;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column()
    private boolean blocked;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String phone;

    private Roles role = Roles.ROLE_CLIENT;

}


