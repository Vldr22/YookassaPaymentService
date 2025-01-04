package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.request.TransactionFilterRequest;
import com.education.mypaymentservice.model.response.TransactionResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.service.forController.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/getAllClients")
    public List<ClientResponse> getAllClients() {
        return employeeService.getAllClientsResponses();
    }

    @GetMapping("/getSettings")
    public AppSetting getSettings() {
        return employeeService.getAppSetting();
    }

    @PostMapping("/getFilteredTransactions")
    public ResponseEntity<List<TransactionResponse>> getFilteredTransactions(
            @RequestBody TransactionFilterRequest filterRequest) {
        List<TransactionResponse> transactionList = employeeService.getFilteredTransactionsResponses(filterRequest);
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }
}
