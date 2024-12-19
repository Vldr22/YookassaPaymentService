package com.education.mypaymentservice.service.transactionService;

import com.education.mypaymentservice.dto.Transaction;
import com.education.mypaymentservice.model.TransactionStatus;
import com.education.mypaymentservice.service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final BigDecimal feePercent;
    private final TransactionRepository transactionRepository;

    public TransactionService(@Value("${FEE_PERCENT}") BigDecimal feePercent,
                              TransactionRepository transactionRepository) {
        this.feePercent = feePercent;
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(Transaction transaction) {
        transaction.setFeePercent(feePercent);
        transaction.setFee(calculateAndSetFee(feePercent, transaction.getAmount()));

        Optional<Transaction> saveTransaction = Optional.of(transactionRepository.save(transaction));
        return saveTransaction.orElseThrow(() -> new RuntimeException("Ошибка при добавлении транзакции!!!"));
    }

    public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transaction.setUpdate_date(LocalDateTime.now());

        Optional<Transaction> saveTransaction = Optional.of(transactionRepository.save(transaction));
        return saveTransaction.orElseThrow(() -> new RuntimeException("Ошибка при обновлении транзакции!!!"));
    }

    public Transaction findTransactionById(UUID transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.orElseThrow(() -> new RuntimeException(" Транзакция с id: " + transactionId + " не найдена"));
    }

    private BigDecimal calculateAndSetFee(BigDecimal feePercent, BigDecimal amount) {
        if (amount == null || feePercent == null) {
            throw new IllegalArgumentException("Amount и feePercent не могут иметь значение null");
        }
        return amount.multiply(feePercent).setScale(2, RoundingMode.HALF_UP);
    }
}
