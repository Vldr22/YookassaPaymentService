package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.model.response.TransactionResponse;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.common.TransactionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Getter
    private final YookassaPaymentService yookassaPaymentService;

    private final TransactionService transactionService;

    public List<TransactionResponse> getAllClientTransactionsResponse() {
        Client client = yookassaPaymentService.getAuthorizationClient();
        List<Transaction> transactionList = transactionService.findTransactionByPhone(client.getPhone());

        return transactionList.stream().map(transaction -> {
            return TransactionResponse.builder()
                            .amount(transaction.getAmount())
                            .currency(transaction.getCurrency())
                            .status(transaction.getStatus())
                            .createDate(transaction.getCreateDate()).build();
        }).toList();
    }
}
