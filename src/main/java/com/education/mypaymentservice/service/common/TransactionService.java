package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.repository.TransactionRepository;
import com.education.mypaymentservice.model.entity.filters.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AppSettingSingleton appSettingSingleton;

    private BigDecimal calculateAndSetFee(BigDecimal feePercent, BigDecimal amount) {
        if (amount == null || feePercent == null) {
            throw new IllegalArgumentException("Amount или feePercent не могут иметь значение null: " + amount);
        }
        return amount.multiply(feePercent).setScale(2, RoundingMode.HALF_UP);
    }

    public Transaction addTransaction(Transaction transaction) {

        if (transaction.getCreateDate() == null) {
            transaction.setCreateDate(LocalDateTime.now());
        } else {
            transaction.setUpdateDate(LocalDateTime.now());
        }

        BigDecimal feePercent = appSettingSingleton.getAppSetting().getFeePercent();
        transaction.setFeePercent(feePercent);
        transaction.setFee(calculateAndSetFee(feePercent, transaction.getAmount()));

        Optional<Transaction> saveTransaction = Optional.of(transactionRepository.save(transaction));
        return saveTransaction.orElseThrow(() -> new PaymentServiceException("Ошибка при добавлении транзакции: "
                + transaction));
    }

    public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transaction.setUpdateDate(LocalDateTime.now());

        Optional<Transaction> saveTransaction = Optional.of(transactionRepository.save(transaction));
        return saveTransaction.orElseThrow(() -> new PaymentServiceException("Ошибка при обновлении транзакции: "
                + transaction));
    }

    public List<Transaction> findTransactionByPhone(String phone) {
       return transactionRepository.findAllByClient_Phone(phone);
    }

    public List<Transaction> getFilteredTransactions(String phone, LocalDateTime startDate, LocalDateTime endDate,
                                                     BigDecimal minAmount, BigDecimal maxAmount) {
        Specification<Transaction> specification = Specification.where(TransactionSpecification.byClientPhone(phone))
                .and(TransactionSpecification.byCreateDateRange(startDate, endDate))
                .and(TransactionSpecification.byAmountRange(minAmount, maxAmount));

        return transactionRepository.findAll(specification);
    }

    public List<Transaction> findTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
}
