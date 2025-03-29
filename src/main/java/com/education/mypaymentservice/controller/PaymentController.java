package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.request.SubscriptionRequest;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.response.ClientTransactionResponse;
import com.education.mypaymentservice.model.response.SubscriptionResponse;
import com.education.mypaymentservice.model.yookassa.Confirmation;
import com.education.mypaymentservice.service.forController.PaymentService;
import com.education.mypaymentservice.service.yookassa.YookassaDefaultService;
import com.education.mypaymentservice.service.yookassa.YookassaSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final YookassaDefaultService yookassaSimplePaymentService;
    private final YookassaSubscriptionService yookassaSubscriptionService;

    @PostMapping()
    public CommonResponse<Confirmation> createPayment(@RequestBody CreatePaymentRequest request) {
        Confirmation confirmationUrl = yookassaSimplePaymentService.paymentResponse(request);
        return CommonResponse.success(confirmationUrl);
    }

    @GetMapping("/findClientTransactions")
    public CommonResponse<List<ClientTransactionResponse>> findClientTransactions() {
        List<ClientTransactionResponse> responseList = paymentService.getAllClientTransactionsResponse();
        return CommonResponse.success(responseList);
    }

    @PostMapping("/subscribe")
    public CommonResponse<SubscriptionResponse> subscribe(@RequestBody @Valid SubscriptionRequest request) {
        SubscriptionResponse response = yookassaSubscriptionService.paymentResponse(request);
        return CommonResponse.success(response);
    }
}
