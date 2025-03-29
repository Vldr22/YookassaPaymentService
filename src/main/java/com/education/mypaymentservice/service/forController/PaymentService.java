package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.model.response.ClientTransactionResponse;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.yookassa.YookassaDefaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final YookassaDefaultService yookassaSimplePaymentService;

    private final TransactionService transactionService;

    public List<ClientTransactionResponse> getAllClientTransactionsResponse() {
        Client client =  yookassaSimplePaymentService.getAuthorizationClient();

        String validPhone = normalizeRussianPhoneNumber(client.getPhone());
        List<Transaction> transactionList = transactionService.findByPhone(validPhone);

        return transactionList.stream().map(transaction -> {
            return ClientTransactionResponse.builder()
                            .amount(transaction.getAmount())
                            .currency(transaction.getCurrency())
                            .status(transaction.getStatus())
                            .createDate(transaction.getCreateDate()).build();
        }).toList();
    }
}
