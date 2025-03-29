package com.education.mypaymentservice.service.yookassa;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.SubscriptionType;
import com.education.mypaymentservice.model.request.SubscriptionRequest;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.response.SubscriptionResponse;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentResponse;
import com.education.mypaymentservice.model.yookassa.Amount;
import com.education.mypaymentservice.model.yookassa.Subscription;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentRequest;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import com.education.mypaymentservice.service.forController.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class YookassaSubscriptionService extends YookassaAbstractService
        <SubscriptionRequest, SubscriptionResponse> {

    private final int amountSubscription;
    private final int interval;
    private final SubscriptionService subscriptionService;

    public YookassaSubscriptionService(YookassaFeignClient yookassaFeignClient, ClientService clientService,
                                       CardTokenService cardTokenService, TransactionService transactionService,
                                       @Value("${yookassa.shopId}") String shopId,
                                       @Value("${yookassa.secret.key}") String apiKey,
                                       SubscriptionService subscriptionService,
                                       @Value("${cost.subscription.}") int amountSubscription,
                                       @Value("${interval.subscription}") int interval,
                                       EmployeeService employeeService) {
        super(yookassaFeignClient, clientService, cardTokenService, transactionService, shopId, apiKey, employeeService);
        this.amountSubscription = amountSubscription;
        this.subscriptionService = subscriptionService;
        this.interval = interval;
    }

    private CreatePaymentRequest createPaymentRequest() {
        return new CreatePaymentRequest(
                new Amount(BigDecimal.valueOf(amountSubscription), Currency.RUB),
                "http:12345",
                "Оплата подписки");
    }

    @Override
    public YookassaPaymentRequest createPayment(SubscriptionRequest subscriptionRequest) {

        CreatePaymentRequest paymentRequest = createPaymentRequest();

        var yookassaPaymentRequest = createPaymentRequestBuilder(paymentRequest);

        yookassaPaymentRequest
                .description(paymentRequest.description())
                .subscription(Subscription.builder()
                        .interval(String.valueOf(interval))
                        .period(subscriptionRequest.getPeriodInMonths())
                        .startDate(LocalDate.now().toString())
                        .build())
                .build();
        return yookassaPaymentRequest.build();
    }

    @Override
    public SubscriptionResponse paymentResponse(SubscriptionRequest request) {
        Client client = getAuthorizationClient();
        YookassaPaymentRequest paymentRequest = createPayment(request);

        ResponseEntity<YookassaPaymentResponse> response =
                yookassaFeignClient.createPayment(createHeaders(), paymentRequest);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            System.out.println(response.getBody().getConfirmation().confirmationUrl);

            addPaymentToDatabase(response);

            if (subscriptionService.getSubscriptionByClient_Id(client.getId()) == null) {
                addSubscriptionToDatabase(request, response.getBody());
            } else {
                subscriptionService.updateSubscriptionInDatabase(subscriptionService.getSubscriptionByClient_Id(client.getId()));
            }

            return getSubscriptionResponse(request, response);
        } else {
            throw new PaymentServiceException("Ошибка при создании платежа: ", "response",
                    Objects.requireNonNull(response.getBody()).toString());

        }
    }

    private static SubscriptionResponse getSubscriptionResponse(SubscriptionRequest request, ResponseEntity<YookassaPaymentResponse> response) {
        SubscriptionResponse result;
        if (Objects.requireNonNull(response.getBody()).getConfirmation().confirmationUrl != null) {
            result = new SubscriptionResponse(
                    "Для оформления подписки произведите оплату!",
                    response.getBody().getConfirmation().confirmationUrl,
                    SubscriptionType.DEFAULT,
                    request.getPeriodInMonths());
        } else {
            result = new SubscriptionResponse(
                    "Подписка успешно оформлена!",
                    null,
                    SubscriptionType.DEFAULT,
                    request.getPeriodInMonths());
        }
        return result;
    }

    private void addSubscriptionToDatabase(SubscriptionRequest request, YookassaPaymentResponse response) {
        subscriptionService.addSubscriptionToDatabase(
                getAuthorizationClient(),
                request.getSubscriptionType(),
                response.getCreatedAt().toLocalDate(),
                response.getCreatedAt().toLocalDate().plusMonths(request.getPeriodInMonths())
        );
    }
}





