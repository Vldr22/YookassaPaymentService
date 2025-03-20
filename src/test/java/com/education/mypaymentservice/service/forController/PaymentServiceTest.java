package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.response.ClientTransactionResponse;
import com.education.mypaymentservice.service.common.TransactionService;
import com.education.mypaymentservice.service.model.TestModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private YookassaPaymentService yookassaPaymentService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentService paymentService;

    private Client testClient;
    private Transaction testTransaction;
    private String normalizedPhone;

    @BeforeEach
    void setUp() {
        testClient = TestModelFactory.createTestClient();
        testClient.setRegistrationDate(LocalDateTime.now());
        testClient.setBlocked(false);

        normalizedPhone = normalizeRussianPhoneNumber(testClient.getPhone());

        testTransaction = TestModelFactory.createTestTransaction(BigDecimal.valueOf(100));
        testTransaction.setCreateDate(LocalDateTime.now());
        testTransaction.setUpdateDate(LocalDateTime.now().plusHours(2));
    }

    @Test
    public void getAllClientTransactionsResponse_ShouldReturnClientTransactions() {

        when(yookassaPaymentService.getAuthorizationClient()).thenReturn(testClient);
        when(transactionService.findByPhone(normalizedPhone)).thenReturn(Collections.singletonList(testTransaction));

        List<ClientTransactionResponse> responses = paymentService.getAllClientTransactionsResponse();

        assertEquals(1, responses.size());
        ClientTransactionResponse response = responses.get(0);
        assertEquals(testTransaction.getAmount(), response.getAmount());
        assertEquals(testTransaction.getCurrency(), response.getCurrency());
        assertEquals(testTransaction.getStatus(), response.getStatus());
        assertEquals(testTransaction.getCreateDate(), response.getCreateDate());
    }
}