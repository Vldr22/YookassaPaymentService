package com.education.mypaymentservice.service;

import com.education.mypaymentservice.dto.CardToken;
import com.education.mypaymentservice.dto.Client;
import com.education.mypaymentservice.dto.Transaction;
import com.education.mypaymentservice.exceptionHandler.PaymentServiceException;
import com.education.mypaymentservice.model.PaymentRequest;
import com.education.mypaymentservice.model.PaymentResponse;
import com.education.mypaymentservice.model.TransactionStatus;
import com.education.mypaymentservice.service.cardTockenService.CardTokenService;
import com.education.mypaymentservice.service.transactionService.TransactionService;
import com.education.mypaymentservice.service.clientService.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class YookassaPaymentService {

    private final RestTemplate restTemplate;
    private final String shopId;
    private final String secretKey;
    private final String apiBaseUrl;

    private final ClientService clientService;
    private final CardTokenService cardTokenService;
    private final TransactionService transactionService;

    public YookassaPaymentService(RestTemplate restTemplate,
                                  @Value("${YOOKASSA_SHOP_ID}") String shopId,
                                  @Value("${YOOKASSA_SECRET_KEY}") String apiKey,
                                  @Value("${YOOKASSA_API_URL}") String apiBaseUrl,
                                  ClientService clientService, CardTokenService cardTokenService,
                                  TransactionService transactionService) {

        this.restTemplate = restTemplate;
        this.shopId = shopId;
        this.secretKey = apiKey;
        this.apiBaseUrl = apiBaseUrl;
        this.clientService = clientService;
        this.cardTokenService = cardTokenService;
        this.transactionService = transactionService;
    }

    private HttpHeaders createHeaders() {
        String auth = shopId + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Idempotence-Key", UUID.randomUUID().toString());
        return headers;
    }

    private PaymentRequest createPaymentRequest(BigDecimal amount, Currency currency,
                                                String description, String returnUrl) {

        PaymentRequest paymentRequest = new PaymentRequest();

        PaymentRequest.Amount paymentAmount = new PaymentRequest.Amount();
        paymentAmount.setValue(amount);
        paymentAmount.setCurrency(currency);
        paymentRequest.setAmount(paymentAmount);

        PaymentRequest.Confirmation confirmation = new PaymentRequest.Confirmation();
        confirmation.setType("redirect");
        confirmation.setReturnUrl(returnUrl);
        paymentRequest.setConfirmation(confirmation);

        paymentRequest.setDescription(description);
        paymentRequest.setCapture(true);
        paymentRequest.setSavePaymentMethod(true);
        return paymentRequest;
    }

    private void addPaymentToDatabase(ResponseEntity<PaymentResponse> response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Пользователь не авторизован");
        }

        String phone = authentication.getPrincipal().toString();
        Client client = clientService.findClientByPhone(phone);

        if (!client.isBlocked()) {
            CardToken cardToken = cardTokenService.addCardToken(new CardToken(
                    Objects.requireNonNull(response.getBody()).getId().toString(), client));

            transactionService.addTransaction(new Transaction(
                    LocalDateTime.now(),
                    Objects.requireNonNull(response.getBody()).getAmount().getValue(),
                    response.getBody().getAmount().getCurrency(),
                    TransactionStatus.IN_PROGRESS,
                    client,
                    cardToken));
        } else {
            throw new PaymentServiceException("Клиент имеет статус заблокированного! Операция отменена ");
        }
    }

    public String createPaymentForToken(BigDecimal amount, Currency currency, String description, String returnUrl) {

        PaymentRequest paymentRequest = createPaymentRequest(amount, currency, description, returnUrl);

        HttpEntity<PaymentRequest> paymentRequestHttpEntity = new HttpEntity<>(paymentRequest, createHeaders());
        ResponseEntity<PaymentResponse> response =
                restTemplate.postForEntity(apiBaseUrl, paymentRequestHttpEntity, PaymentResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

            System.out.println(response.getBody().getConfirmation().getConfirmationUrl());
            System.out.println(response.getBody());
            addPaymentToDatabase(response);

            return response.getBody().getConfirmation().getConfirmationUrl();
        } else {
            throw new RuntimeException("Ошибка при создании платежа: " + response.getStatusCode());
        }
    }

    public void updatePaymentStatus(Transaction transaction, ResponseEntity<PaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful() &&
                Objects.requireNonNull(response.getBody()).getStatus().equals("succeeded")) {
            transactionService.updateTransactionStatus(transaction, TransactionStatus.DONE);
        } else {
            if (response.getStatusCode().is2xxSuccessful() &&
                    Objects.requireNonNull(response.getBody()).getStatus().equals("cancelled")) {
                transactionService.updateTransactionStatus(transaction, TransactionStatus.CANCELED);
            }
            throw new PaymentServiceException("Транзакция с id: " + transaction.getId() + " не проведена! Статус: "
                    + response.getStatusCode());
        }
    }

    public ResponseEntity<PaymentResponse> getPaymentDetails(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор платежа не может быть пустым!!!");
        }

        String url = apiBaseUrl + "/" + paymentId;
        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, PaymentResponse.class);
    }
}



