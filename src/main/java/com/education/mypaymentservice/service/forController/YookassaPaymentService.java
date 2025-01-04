package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.common.Amount;
import com.education.mypaymentservice.model.common.Confirmation;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.request.YookassaPaymentRequest;
import com.education.mypaymentservice.model.response.YookassaPaymentResponse;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class YookassaPaymentService {

    private final YookassaFeignClient yookassaFeignClient;
    private final String shopId;
    private final String secretKey;
    private final ClientService clientService;
    private final CardTokenService cardTokenService;
    private final TransactionService transactionService;

    public YookassaPaymentService(YookassaFeignClient yookassaFeignClient,
                                  @Value("${yookassa.shopId}") String shopId,
                                  @Value("${yookassa.secret.key}") String apiKey,
                                  ClientService clientService, CardTokenService cardTokenService,
                                  TransactionService transactionService) {

        this.yookassaFeignClient = yookassaFeignClient;
        this.shopId = shopId;
        this.secretKey = apiKey;
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
    private YookassaPaymentRequest createYookassaPaymentRequest(CreatePaymentRequest request) {
        return YookassaPaymentRequest.builder()
                .amount(Amount.builder()
                        .value(request.getAmount().getValue())
                        .currency(request.getAmount().getCurrency())
                        .build())
                .confirmation(Confirmation.builder()
                        .type("redirect")
                        .returnUrl(request.getReturnUrl())
                        .build())
                .description(request.getDescription())
                .capture(true)
                .savePaymentMethod(true)
                .build();
    }

    private void addPaymentToDatabase(ResponseEntity<YookassaPaymentResponse> response) {

        Client client = getAuthorizationClient();

        if (!client.isBlocked()) {
            CardToken cardToken = cardTokenService.addCardToken(
                    new CardToken(Objects.requireNonNull(response.getBody()).getId().toString(), client));

            transactionService.addTransaction(new Transaction(
                    response.getBody().getId(),
                    Objects.requireNonNull(response.getBody()).getAmount().getValue(),
                    response.getBody().getAmount().getCurrency(),
                    TransactionStatus.IN_PROGRESS,
                    client,
                    cardToken));
        } else {
            throw new PaymentServiceException("Клиент c телефоном: " + client.getPhone() +
                    " имеет статус заблокированного! Операция отменена");
        }
    }

    public Client getAuthorizationClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Пользователь НЕ авторизован", "authentication",
                    Objects.requireNonNull(authentication).toString());
        }
        String phone = authentication.getPrincipal().toString();
        return clientService.findClientByPhone(phone);
    }

    public String createYookassaPaymentResponse(CreatePaymentRequest request) {
       YookassaPaymentRequest paymentRequest = createYookassaPaymentRequest(request);

        ResponseEntity<YookassaPaymentResponse> response =
                yookassaFeignClient.createPayment(createHeaders(), paymentRequest);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            System.out.println(response.getBody().getConfirmation().confirmationUrl);
            addPaymentToDatabase(response);
            return response.getBody().getConfirmation().getConfirmationUrl();
        } else {
            throw new PaymentServiceException("Ошибка при создании платежа: ", "response",
                    Objects.requireNonNull(response.getBody()).toString());
        }
    }

    public ResponseEntity<YookassaPaymentResponse> getPaymentDetails(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор платежа не может быть пустым или равен null: " + paymentId);
        }
        return yookassaFeignClient.getPaymentDetails(createHeaders(), paymentId);
    }
}



