package com.education.mypaymentservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "card_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CardToken {

    @Id
    @GeneratedValue
    private UUID id;

    @JsonIgnore
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
