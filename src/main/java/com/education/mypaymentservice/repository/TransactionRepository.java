package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, QuerydslPredicateExecutor<Transaction> {

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findAllByClient_Phone(String phone);

}
