package com.education.mypaymentservice.service.model;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TestModelFactory {

    public static Client createTestClient() {
        Client client = new Client();
        client.setName("Иван");
        client.setSurname("Иванов");
        client.setMidname("Иванович");
        client.setPhone("+79001234567");
        return client;
    }

    public static Client createTestClientWithArguments(String phone, String name, String surname) {
            Client client = new Client();
            client.setName(name);
            client.setSurname(surname);
            client.setPhone(phone);
            client.setRole(Roles.ROLE_CLIENT);
            client.setBlocked(true);
            client.setRegistrationDate(LocalDateTime.now());
            return client;
        }

    public static Transaction createTestTransaction(BigDecimal amount) {
            return new Transaction(
                    UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                    amount,
                    Currency.RUB,
                    TransactionStatus.NEW,
                    createTestClient(),
                    null
            );
        }

}
