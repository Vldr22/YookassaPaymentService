package com.education.mypaymentservice.service.yookassa;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentResponse;
import com.education.mypaymentservice.model.yookassa.Confirmation;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentRequest;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class YookassaDefaultService extends YookassaAbstractService<CreatePaymentRequest, Confirmation> {

    public YookassaDefaultService(YookassaFeignClient yookassaFeignClient, ClientService clientService,
                                  CardTokenService cardTokenService, TransactionService transactionService,
                                  @Value("${yookassa.shopId}") String shopId,
                                  @Value("${yookassa.secret.key}") String apiKey,
                                  EmployeeService employeeService) {
        super(yookassaFeignClient, clientService, cardTokenService, transactionService, shopId, apiKey, employeeService);
    }

    @Override
    public YookassaPaymentRequest createPayment(CreatePaymentRequest request) {
        return createPaymentRequestBuilder(request)
                .build();
    }

    @Override
    public Confirmation paymentResponse(CreatePaymentRequest request) {
        YookassaPaymentRequest paymentRequest = createPayment(request);

        ResponseEntity<YookassaPaymentResponse> response =
                yookassaFeignClient.createPayment(createHeaders(), paymentRequest);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            addPaymentToDatabase(response);
            return response.getBody().getConfirmation();
        } else {
            throw new PaymentServiceException("Ошибка при создании платежа: ", "response",
                    Objects.requireNonNull(response.getBody()).toString());

        }
    }
}
