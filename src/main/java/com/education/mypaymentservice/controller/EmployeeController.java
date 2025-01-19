package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.response.EmployeeTransactionResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.service.forController.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/clients")
    public CommonResponse<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clientResponseList = employeeService.getAllClientsResponses();
        return CommonResponse.success(clientResponseList);
    }

    @GetMapping("/settings")
    public CommonResponse<AppSetting> getSettings() {
        AppSetting appSetting = employeeService.getAppSetting();
        return CommonResponse.success(appSetting);
    }

    @PostMapping("/filteredTransactions")
    public CommonResponse<List<EmployeeTransactionResponse>> getFilteredTransactions(
            @RequestBody TransactionFilterRequest filterRequest) {
        List<EmployeeTransactionResponse> transactionList = employeeService.getFilteredTransactionsResponses(filterRequest);
        return CommonResponse.success(transactionList);
    }
}
