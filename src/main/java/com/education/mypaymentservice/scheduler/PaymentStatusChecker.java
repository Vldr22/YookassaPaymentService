package com.education.mypaymentservice.scheduler;

import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.response.YookassaPaymentResponse;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.YookassaPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusChecker {

    private final TransactionService transactionService;
    private final YookassaPaymentService yookassaPaymentService;

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    public void checkPaymentStatuses() {
        List<Transaction> pendingTransactions = transactionService.findTransactionsByStatus(
                TransactionStatus.IN_PROGRESS);

        for (Transaction transaction : pendingTransactions) {
            try {
                ResponseEntity<YookassaPaymentResponse> response = yookassaPaymentService.getPaymentDetails(
                        String.valueOf(transaction.getId())
                );
                updatePaymentStatus(transaction, response);
            } catch (Exception e) {
                log.error("Ошибка при проверке статуса транзакции: ", (Object[]) e.getStackTrace());
            }
        }
    }

    private void updatePaymentStatus(Transaction transaction, ResponseEntity<YookassaPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful() &&
                Objects.requireNonNull(response.getBody()).getStatus().equals("succeeded")) {
            transactionService.updateStatus(transaction, TransactionStatus.DONE);
        } else if (response.getStatusCode().is2xxSuccessful() &&
                Objects.requireNonNull(response.getBody()).getStatus().equals("canceled")) {
            transactionService.updateStatus(transaction, TransactionStatus.CANCELED);
        }
        //Оставил для наблюдения этот блок. Он вероятно тут не нужен
        else {
            log.info("Транзакция с id: {} все еще не проведена! Статус: {}", transaction.getId(), transaction.getStatus());
        }
    }
}

