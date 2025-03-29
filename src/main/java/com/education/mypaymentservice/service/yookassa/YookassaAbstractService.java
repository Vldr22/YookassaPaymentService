package com.education.mypaymentservice.service.yookassa;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.yookassa.Amount;
import com.education.mypaymentservice.model.yookassa.Confirmation;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentResponse;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentRequest;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public abstract class YookassaAbstractService<Req, Resp> {

    protected final YookassaFeignClient yookassaFeignClient;
    protected final ClientService clientService;
    protected final CardTokenService cardTokenService;
    protected final TransactionService transactionService;
    protected final String shopId;
    protected final String apiKey;
    protected final EmployeeService employeeService;

    public YookassaAbstractService(YookassaFeignClient yookassaFeignClient,
                                   ClientService clientService,
                                   CardTokenService cardTokenService,
                                   TransactionService transactionService, String shopId, String apiKey, EmployeeService employeeService) {

        this.yookassaFeignClient = yookassaFeignClient;
        this.clientService = clientService;
        this.cardTokenService = cardTokenService;
        this.transactionService = transactionService;
        this.shopId = shopId;
        this.apiKey = apiKey;
        this.employeeService = employeeService;
    }

    public Client getAuthorizationClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Пользователь НЕ авторизован", "authentication",
                    Objects.requireNonNull(authentication).toString());
        }
        String phone = authentication.getPrincipal().toString();
        return clientService.findByPhone(phone);
    }

    protected HttpHeaders createHeaders() {
        String auth = shopId + ":" + apiKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Idempotence-Key", UUID.randomUUID().toString());
        return headers;
    }

    protected void addPaymentToDatabase(ResponseEntity<YookassaPaymentResponse> response) {

        Client client = getAuthorizationClient();

        if (!client.isBlocked()) {
            CardToken cardToken;
            if (cardTokenService.findValueTokenByClientId(client.getId())!=null) {
                cardToken = cardTokenService.findTokenByClientId(client.getId());
            } else {
                cardToken = null;
            }

            transactionService.add(new Transaction(
                    Objects.requireNonNull(response.getBody()).getId(),
                    Objects.requireNonNull(response.getBody()).getAmount().getValue(),
                    response.getBody().getAmount().getCurrency(),
                    TransactionStatus.IN_PROGRESS,
                    client,
                    cardToken,
                    response.getBody().getDescription()));

        } else {
            throw new PaymentServiceException("Клиент c телефоном: " + client.getPhone() +
                    " имеет статус заблокированного! Операция отменена");
        }
    }

    public ResponseEntity<YookassaPaymentResponse> getPaymentDetails(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("Идентификатор платежа не может быть пустым или равен null: " + paymentId);
        }
        return yookassaFeignClient.getPaymentDetails(createHeaders(), paymentId);
    }

    protected String getClientPaymentToken(Client client) {
        if (cardTokenService.findValueTokenByClientId(client.getId())==null) {
            return cardTokenService.findValueTokenByClientId(client.getId());
        } else {
            return null;
        }
    }

    protected YookassaPaymentRequest.YookassaPaymentRequestBuilder createPaymentRequestBuilder(CreatePaymentRequest request) {

        Client client = getAuthorizationClient();
        String token = getClientPaymentToken(client);

        YookassaPaymentRequest.YookassaPaymentRequestBuilder builder = YookassaPaymentRequest.builder()
                .amount(Amount.builder()
                        .value(request.amount().getValue())
                        .currency(request.amount().getCurrency())
                        .build())
                .description(request.description())
                .capture(true)
                .savePaymentMethod(true);

        if (token != null) {
            builder.paymentMethodId(token);
        } else {
            builder.confirmation(Confirmation.builder()
                    .type("redirect")
                    .returnUrl(request.returnUrl())
                    .build());
        }
        return builder;
    }

    public abstract YookassaPaymentRequest createPayment(Req request);

    public abstract Resp paymentResponse(Req request);

}
