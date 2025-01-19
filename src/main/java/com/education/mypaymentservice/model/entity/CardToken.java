package com.education.mypaymentservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_tokens")
public class CardToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public CardToken(String token, Client client) {
        this.token = token;
        this.client = client;
    }
}
