package com.education.mypaymentservice.scheduler;

import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentResponse;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.SubscriptionService;
import com.education.mypaymentservice.service.yookassa.YookassaDefaultService;
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
    private final YookassaDefaultService yookassaSimplePaymentService;
    private final CardTokenService cardTokenService;
    private final SubscriptionService subscriptionService;

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    public void checkPaymentStatuses() {
        List<Transaction> pendingTransactions = transactionService.findTransactionsByStatus(
                TransactionStatus.IN_PROGRESS);

        for (Transaction transaction : pendingTransactions) {
            try {
                ResponseEntity<YookassaPaymentResponse> response = yookassaSimplePaymentService.getPaymentDetails(
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

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    protected void addTokenToDatabase() {
        List<Transaction> successTransactions = transactionService.findTransactionsByStatus(
                TransactionStatus.DONE);

        for (Transaction transaction : successTransactions) {
            try {
                ResponseEntity<YookassaPaymentResponse> response = yookassaSimplePaymentService.getPaymentDetails(
                        String.valueOf(transaction.getId())
                );

                if (transaction.getCardToken() == null) {
                    String yookassaToken = Objects.requireNonNull(response.getBody()).getPaymentMethod().getId();
                    Client client = transaction.getClient();

                    if (cardTokenService.findValueTokenByClientId(client.getId()) == null) {
                        cardTokenService.add(new CardToken(yookassaToken, client));
                    }
                }

                if (transaction.getDescription().equals("Оплата подписки")
                        && transaction.getStatus() == TransactionStatus.DONE) {
                    System.out.println("МЕТОД ВЫПОЛНЯЕТСЯ");
                    System.out.println("МЕТОД ВЫПОЛНЯЕТСЯ");
                    System.out.println("МЕТОД ВЫПОЛНЯЕТСЯ");
                    activeSubscription(transaction);
                }


            } catch (Exception e) {
                log.error("Ошибка при записи токена: ", (Object[]) e.getStackTrace());
            }
        }
    }

    private void activeSubscription(Transaction transaction)  {
        Client client = transaction.getClient();
        subscriptionService.updateSubscriptionActive(client, true);
    }
}

