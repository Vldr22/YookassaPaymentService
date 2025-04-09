package com.education.mypaymentservice.service.yookassa;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.request.RefundRequest;
import com.education.mypaymentservice.model.response.RefundResponse;
import com.education.mypaymentservice.model.yookassa.*;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class YookassaRefundService extends YookassaAbstractService<RefundRequest, RefundResponse> {


    public YookassaRefundService(YookassaFeignClient yookassaFeignClient,
                                 ClientService clientService, CardTokenService cardTokenService,
                                 TransactionService transactionService,
                                 @Value("${yookassa.shopId}") String shopId,
                                 @Value("${yookassa.secret.key}") String apiKey, EmployeeService employeeService) {
        super(yookassaFeignClient, clientService, cardTokenService, transactionService, shopId, apiKey, employeeService);
    }

    @Override
    public YookassaPaymentRequest createPayment(RefundRequest request) {
       return YookassaPaymentRequest.builder()
                .amount(Amount.builder()
                        .value(request.getAmount().getValue())
                        .currency(request.getAmount().getCurrency())
                        .build())
                .description("Возврат средств")
                .capture(true)
                .savePaymentMethod(true)
               /*По идеи должна быть привязанная карта магазина и отправлена на саму ЮКАССУ. Я НЕ СТАЛ делать
               логику через уже добавления в БД карты или привязки кошелька по ID. Оно будет работать
               с любым привязанным кошельком, как и в случае с другими платежами если указать payment_id.
               Или привязать карту. Тестовые карты которые предоставляет ЮКАССА не явл таковыми
               Пока оставил так для более удобных тестов себе. Если надо сделаю привязку и отдельный счет магазина
               */
                .confirmation(Confirmation.builder()
                        .type("redirect")
                        .returnUrl("http://localhost:8484")
                        .build())
                .build();
    }

    @Override
    public RefundResponse paymentResponse(RefundRequest request) {

        Transaction transaction = transactionService.findTransactionById(UUID.fromString(request.getPaymentId()));

        YookassaPaymentRequest refundRequest = createPayment(request);

        ResponseEntity<YookassaPaymentResponse> response =
                yookassaFeignClient.createPayment(createHeaders(), refundRequest);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            transactionService.updateStatus(transaction, TransactionStatus.NEW);

            System.out.println(response.getBody().getConfirmation().confirmationUrl);
            return RefundResponse.builder()
                    .id(String.valueOf(response.getBody().getId()))
                    .amount(response.getBody().getAmount())
                    .createdAt(response.getBody().getCreatedAt())
                    .status(response.getBody().getStatus())
                    .build();
        } else {
            throw new PaymentServiceException("Ошибка при возврате средств", "response",
                    Objects.requireNonNull(response.getBody()).toString());
        }
    }
}
