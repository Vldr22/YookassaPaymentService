package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.QTransaction;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.repository.TransactionRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AppSettingService appSettingService;

    private BigDecimal calculateAndSetFee(BigDecimal feePercent, BigDecimal amount) {
        if (amount == null || feePercent == null) {
            throw new IllegalArgumentException("Amount или feePercent не могут иметь значение null: " + amount);
        }
        return amount.multiply(feePercent).setScale(2, RoundingMode.HALF_UP);
    }

    public Transaction add(Transaction transaction) {

        if (transaction.getCreateDate() == null) {
            transaction.setCreateDate(LocalDateTime.now());
        } else {
            transaction.setUpdateDate(LocalDateTime.now());
        }

        try {
            BigDecimal feePercent = appSettingService.getFeePercent();
            transaction.setFeePercent(feePercent);
            transaction.setFee(calculateAndSetFee(feePercent, transaction.getAmount()));

            return transactionRepository.save(transaction);
        } catch (PaymentServiceException e) {
            throw new PaymentServiceException("Ошибка при добавлении транзакции: "
                    + transaction);
        }
    }

    public Transaction updateStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transaction.setUpdateDate(LocalDateTime.now());

        Optional<Transaction> saveTransaction = Optional.of(transactionRepository.save(transaction));
        return saveTransaction.orElseThrow(() -> new PaymentServiceException("Ошибка при обновлении транзакции: "
                + transaction));
    }

    public List<Transaction> findByPhone(String phone) {
       return transactionRepository.findAllByClient_Phone(phone);
    }

    public List<Transaction> getFilteredTransactions(String phone, LocalDateTime startDate, LocalDateTime endDate,
                                                    BigDecimal minAmount, BigDecimal maxAmount) {

        BooleanBuilder builder = new BooleanBuilder();
        QTransaction qTransaction = QTransaction.transaction;

        if (phone!=null) {
            builder.and(qTransaction.client.phone.eq(normalizeRussianPhoneNumber(phone)));
        }

        if (minAmount != null) {
            builder.and(qTransaction.amount.goe(minAmount));
        }

        if (maxAmount != null) {
            builder.and(qTransaction.amount.loe(maxAmount));
        }

        if (startDate != null) {
            builder.and(qTransaction.createDate.goe(startDate));
        }

        if (endDate != null) {
            builder.and(qTransaction.createDate.loe(endDate));
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        return (List<Transaction>) transactionRepository.findAll(builder, sort);
    }

    public List<Transaction> findTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
}
