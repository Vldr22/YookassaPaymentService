package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.response.ClientTransactionResponse;
import com.education.mypaymentservice.service.forController.PaymentService;
import com.education.mypaymentservice.service.forController.YookassaPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final YookassaPaymentService yookassaPaymentService;

    @PostMapping()
    public CommonResponse<String> createPayment(@RequestBody CreatePaymentRequest request) {
        String confirmationUrl = yookassaPaymentService.createYookassaPaymentResponse(
                request);
        return CommonResponse.success(confirmationUrl);
    }

    @GetMapping("/findClientTransactions")
    public CommonResponse<List<ClientTransactionResponse>> findClientTransactions() {
        List<ClientTransactionResponse> responseList = paymentService.getAllClientTransactionsResponse();
        return CommonResponse.success(responseList);
    }
}
