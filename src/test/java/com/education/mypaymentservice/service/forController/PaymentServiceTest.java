package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.response.ClientTransactionResponse;
import com.education.mypaymentservice.service.common.TransactionService;
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
import java.util.UUID;

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
        testClient = new Client();
        testClient.setName("Иван");
        testClient.setSurname("Иванов");
        testClient.setMidname("Иванович");
        testClient.setPhone("+79001234567");
        testClient.setRegistrationDate(LocalDateTime.now());
        testClient.setBlocked(false);

        normalizedPhone = normalizeRussianPhoneNumber(testClient.getPhone());

        testTransaction = new Transaction();
        testTransaction.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        testTransaction.setCreateDate(LocalDateTime.now());
        testTransaction.setUpdateDate(LocalDateTime.now());
        testTransaction.setAmount(BigDecimal.valueOf(1000));
        testTransaction.setCurrency(Currency.RUB);
        testTransaction.setStatus(TransactionStatus.DONE);
        testTransaction.setFee(BigDecimal.valueOf(10));
        testTransaction.setFeePercent(BigDecimal.valueOf(1));
        testTransaction.setClient(testClient);
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