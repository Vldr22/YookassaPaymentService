package com.education.mypaymentservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public CardToken(String token, Client client) {
        this.token = token;
        this.client = client;
    }
}
