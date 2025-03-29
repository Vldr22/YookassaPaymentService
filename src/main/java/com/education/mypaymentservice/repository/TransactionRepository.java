package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, QuerydslPredicateExecutor<Transaction> {

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findAllByClient_Phone(String phone);

    List<Transaction> findAllByDescription(String description);

    @Query("SELECT t FROM Transaction t WHERE t.description = :description AND t.status = :status")
    List<Transaction> findTransactionsByDescriptionAndStatus(
            @Param("description") String description,
            @Param("status") TransactionStatus status
    );

    @Query("SELECT t FROM Transaction t WHERE t.client = :clientId " +
            "AND t.amount = :amount AND t.currency = :currency AND t.description =:description")
    Optional<Transaction> findTransactionByClientAndAmountAndCurrencyAndDescription(
            @Param("clientId") UUID clientId,
            @Param("amount") BigDecimal amount,
            @Param("currency") Currency currency,
            @Param("description") String description);

    Optional<Transaction> findById(UUID id);

}
