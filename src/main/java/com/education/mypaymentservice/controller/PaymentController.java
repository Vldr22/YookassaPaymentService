package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.response.TransactionResponse;
import com.education.mypaymentservice.service.forController.PaymentService;
import com.education.mypaymentservice.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Void> createPayment(@RequestBody CreatePaymentRequest request) {

        String confirmationUrl = paymentService.getYookassaPaymentService().createYookassaPaymentResponse(
                request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, confirmationUrl)
                .build();
    }

    @GetMapping("/findClientTransactions")
    @JsonView(Views.ForClient.class)
    public ResponseEntity<List<TransactionResponse>> findClientTransactions() {
        List<TransactionResponse> transactionResponses = paymentService.getAllClientTransactionsResponse();
        return ResponseEntity.ok(transactionResponses);
    }

}
