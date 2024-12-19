package com.education.mypaymentservice.dto;

import com.education.mypaymentservice.dto.convertor.BigDecimalToLongConverter;
import com.education.mypaymentservice.model.TransactionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime create_date;

    private LocalDateTime update_date;

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
    @JoinColumn(name = "card_token_id")
    private CardToken cardToken;

    public Transaction(UUID id, LocalDateTime create_date, LocalDateTime update_date, BigDecimal amount, Currency currency,
                       TransactionStatus status, BigDecimal fee, BigDecimal feePercent, Client client, CardToken cardToken) {
        this.id = id;
        this.create_date = create_date;
        this.update_date = update_date;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.fee = fee;
        this.feePercent = feePercent;
        this.client = client;
        this.cardToken = cardToken;
    }

    public Transaction(LocalDateTime create_date, BigDecimal amount, Currency currency,
                       TransactionStatus status, Client client, CardToken cardToken) {
        this.create_date = create_date;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.client = client;
        this.cardToken = cardToken;
    }

    public Transaction() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreate_date() {
        return create_date;
    }

    public void setCreate_date(LocalDateTime create_date) {
        this.create_date = create_date;
    }

    public LocalDateTime getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(LocalDateTime update_date) {
        this.update_date = update_date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFeePercent() {
        return feePercent;
    }

    public void setFeePercent(BigDecimal feePercent) {
        this.feePercent = feePercent;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CardToken getCardToken() {
        return cardToken;
    }

    public void setCardToken(CardToken cardToken) {
        this.cardToken = cardToken;
    }
}
