package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.utils.BigDecimalToLongConverter;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @Convert(converter = BigDecimalToLongConverter.class)
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", nullable = false)
    private TransactionStatus status;

    @Convert(converter = BigDecimalToLongConverter.class)
    @Column(nullable = false)
    private BigDecimal fee;

    @Column(nullable = false, precision = 7, scale = 4)
    private BigDecimal feePercent;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "card_token_id")
    private CardToken cardToken;

    public Transaction(UUID id, BigDecimal amount, Currency currency,
                       TransactionStatus status, Client client, CardToken cardToken) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.client = client;
        this.cardToken = cardToken;
    }
}
