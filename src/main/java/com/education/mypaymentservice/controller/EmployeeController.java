package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.request.RefundRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.response.EmployeeTransactionResponse;
import com.education.mypaymentservice.model.response.FeePercentResponse;
import com.education.mypaymentservice.model.response.RefundResponse;
import com.education.mypaymentservice.service.common.AppSettingService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import com.education.mypaymentservice.service.yookassa.YookassaRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AppSettingService appSettingService;
    private final YookassaRefundService yookassaRefundService;

    @GetMapping("/clients")
    public CommonResponse<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clientResponseList = employeeService.getAllClientsResponses();
        return CommonResponse.success(clientResponseList);
    }

    @GetMapping("/feePercent")
    public CommonResponse<FeePercentResponse> getSettings() {
        BigDecimal feePercent = appSettingService.getFeePercent();
        FeePercentResponse appSetting = new FeePercentResponse(feePercent);
        return CommonResponse.success(appSetting);
    }

    @PostMapping("/filteredTransactions")
    public CommonResponse<List<EmployeeTransactionResponse>> getFilteredTransactions(
            @RequestBody TransactionFilterRequest filterRequest) {
        List<EmployeeTransactionResponse> transactionList = employeeService.getFilteredTransactionsResponses(filterRequest);
        return CommonResponse.success(transactionList);
    }

    @PostMapping("/refund")
    public CommonResponse<RefundResponse> getFilteredTransactions(
            @RequestBody RefundRequest refundRequest) {
        RefundResponse response = yookassaRefundService.paymentResponse(refundRequest);
        return CommonResponse.success(response);
    }
}
