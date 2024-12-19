package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.dto.Transaction;
import com.education.mypaymentservice.exceptionHandler.PaymentServiceException;
import com.education.mypaymentservice.model.PaymentResponse;
import com.education.mypaymentservice.service.transactionService.TransactionService;
import com.education.mypaymentservice.service.YookassaPaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final YookassaPaymentService yookassaPaymentService;
    private final TransactionService transactionService;

    public PaymentController(YookassaPaymentService yookassaPaymentService, TransactionService transactionService) {
        this.yookassaPaymentService = yookassaPaymentService;
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPayment(@RequestParam BigDecimal amount,
                                              @RequestParam Currency currency,
                                              @RequestParam String description,
                                              @RequestParam String returnUrl) {
        String confirmationUrl = yookassaPaymentService.createPaymentForToken(amount, currency, description, returnUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, confirmationUrl)
                .build();
    }

    @PostMapping("/update-status")
    public ResponseEntity<String> updateTransactionStatus(@RequestParam UUID transactionId) {

        Transaction transaction = transactionService.findTransactionById(transactionId);
        ResponseEntity<PaymentResponse> response = yookassaPaymentService.getPaymentDetails(
                transaction.getCardToken().getToken());

        try {
            yookassaPaymentService.updatePaymentStatus(transaction, response);
            return ResponseEntity.ok("Статус транзакции обновлен в базе данных!");
        } catch (PaymentServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при обновлении статуса: " + e.getMessage());
        }
    }
}
