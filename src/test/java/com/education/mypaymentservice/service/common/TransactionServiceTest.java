package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AppSettingService appSettingService;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private BigDecimal feePercent;
    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client("Владимир", "Путин", "Владимирович", "+79001234567");

        testTransaction = new Transaction(
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.RUB,
                TransactionStatus.IN_PROGRESS,
                testClient,
                null
        );

        feePercent = new BigDecimal("0.02");
    }

    @Test
    void add_ShouldCalculateCorrectFee() {
        BigDecimal expectedFee = new BigDecimal("2.00");

        when(appSettingService.getFeePercent()).thenReturn(feePercent);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.add(testTransaction);
        assertEquals(expectedFee, result.getFee());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    public void add_WithValidAmount_ShouldReturnTransaction() {
        when(appSettingService.getFeePercent()).thenReturn(feePercent);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.add(testTransaction);

        assertEquals(testTransaction.getId(), result.getId());
        assertEquals(feePercent, result.getFeePercent());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateStatus_WithValidValue_ShouldReturnUpdateTransaction() {
        TransactionStatus newStatus = TransactionStatus.DONE;
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.updateStatus(testTransaction, newStatus);

        assertEquals(newStatus, result.getStatus());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void findByPhone_WithValidPhone_ShouldReturnTransactions() {
        String phone = testTransaction.getClient().getPhone();

        List<Transaction> expectedTransactions = List.of(
                createTestTransaction(new BigDecimal("10.00")),
                createTestTransaction(new BigDecimal("20.00")),
                createTestTransaction(new BigDecimal("30.00"))
        );

        when(transactionRepository.findAllByClient_Phone(phone)).thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.findByPhone(phone);

        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findAllByClient_Phone(phone);
    }

    @Test
    void findTransactionsByStatus_ShouldReturnTransactionsWithGivenStatus() {
        TransactionStatus status = testTransaction.getStatus();
        List<Transaction> expectedTransactions = List.of(testTransaction);
        when(transactionRepository.findByStatus(status)).thenReturn(expectedTransactions);

        List<Transaction> result = transactionService.findTransactionsByStatus(status);

        assertEquals(expectedTransactions.size(), result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(transactionRepository).findByStatus(status);
    }

    private Transaction createTestTransaction(BigDecimal amount) {
        return new Transaction(
                UUID.randomUUID(),
                amount,
                Currency.RUB,
                TransactionStatus.NEW,
                testClient,
                null
        );
    }
}
