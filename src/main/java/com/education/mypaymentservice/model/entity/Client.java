package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role = Roles.ROLE_CLIENT;

    public Client(String name, String surname, String midname, String phone) {
        this.name = name;
        this.surname = surname;
        this.midname = midname;
        this.phone = phone;
    }
}


