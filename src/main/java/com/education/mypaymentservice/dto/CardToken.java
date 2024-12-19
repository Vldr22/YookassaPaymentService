package com.education.mypaymentservice.dto;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "card_tokens")
public class CardToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public CardToken(UUID id, String token, Client client) {
        this.id = id;
        this.token = token;
        this.client = client;
    }

    public CardToken() {}

    public CardToken(String token, Client client) {
        this.token = token;
        this.client = client;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
