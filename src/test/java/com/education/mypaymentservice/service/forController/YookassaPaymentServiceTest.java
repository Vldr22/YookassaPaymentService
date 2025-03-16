package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.config.YookassaFeignClient;
import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Transaction;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.request.YookassaPaymentRequest;
import com.education.mypaymentservice.model.response.YookassaPaymentResponse;
import com.education.mypaymentservice.model.yookassa.Amount;
import com.education.mypaymentservice.model.yookassa.Confirmation;
import com.education.mypaymentservice.service.common.CardTokenService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.common.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YookassaPaymentServiceTest {

    @Mock
    private YookassaFeignClient yookassaFeignClient;

    @Mock
    private ClientService clientService;

    @Mock
    private CardTokenService cardTokenService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private YookassaPaymentService yookassaPaymentService;

    private Client testClient;
    private CreatePaymentRequest createPaymentRequest;
    private YookassaPaymentResponse yookassaPaymentResponse;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(yookassaPaymentService, "shopId", "test-shop-id");
        ReflectionTestUtils.setField(yookassaPaymentService, "secretKey", "test-secret-key");

        testClient = new Client();
        testClient.setName("Иван");
        testClient.setSurname("Иванов");
        testClient.setMidname("Иванович");
        testClient.setPhone("+79001234567");
        testClient.setBlocked(false);

        paymentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Amount amount = new Amount(BigDecimal.valueOf(1000), Currency.RUB);

        createPaymentRequest = new CreatePaymentRequest(
                amount,
                "https://return-url.com",
                "Тестовый платеж"
        );

        Confirmation confirmation = Confirmation.builder()
                .type("redirect")
                .returnUrl("https://return-url.com")
                .confirmationUrl("https://confirmation-url.com")
                .build();

        yookassaPaymentResponse = new YookassaPaymentResponse();
        yookassaPaymentResponse.setId(paymentId);
        yookassaPaymentResponse.setAmount(amount);
        yookassaPaymentResponse.setConfirmation(confirmation);

        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    public void createHeaders_ShouldReturnCorrectHttpHeaders() {
        HttpHeaders headers = ReflectionTestUtils.invokeMethod(
                yookassaPaymentService, "createHeaders");

        assertNotNull(headers);
        assertTrue(headers.containsKey("Authorization"));
        assertTrue(headers.containsKey("Idempotence-Key"));
        assertEquals("application/json", Objects.requireNonNull(headers.getContentType()).toString());
    }

    @Test
    public void getAuthorizationClient_ShouldReturnClientWhenAuthenticated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "+79001234567", null, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clientService.findByPhone("+79001234567")).thenReturn(testClient);

        Client client = yookassaPaymentService.getAuthorizationClient();

        assertEquals(testClient, client);
        verify(clientService).findByPhone("+79001234567");
    }

    @Test
    public void getAuthorizationClient_ShouldThrowNullPointerExceptionWhenAuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            yookassaPaymentService.getAuthorizationClient();
        });
    }

    @Test
    public void getAuthorizationClient_ShouldThrowUnauthorizedExceptionWhenNotAuthenticated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "+79001234567", null, Collections.emptyList());
        authentication = spy(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> {
            yookassaPaymentService.getAuthorizationClient();
        });
    }

    @Test
    public void createYookassaPaymentResponse_ShouldReturnConfirmationWhenSuccessful() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "+79001234567", null, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clientService.findByPhone("+79001234567")).thenReturn(testClient);

        ResponseEntity<YookassaPaymentResponse> responseEntity =
                new ResponseEntity<>(yookassaPaymentResponse, HttpStatus.OK);
        when(yookassaFeignClient.createPayment(any(HttpHeaders.class), any(YookassaPaymentRequest.class)))
                .thenReturn(responseEntity);

        when(cardTokenService.add(any(CardToken.class))).thenReturn(new CardToken());
        when(transactionService.add(any(Transaction.class))).thenReturn(new Transaction());

        Confirmation confirmation = yookassaPaymentService.createYookassaPaymentResponse(createPaymentRequest);

        assertNotNull(confirmation);
        assertEquals("https://confirmation-url.com", confirmation.confirmationUrl);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService).add(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(paymentId, capturedTransaction.getId());
        assertEquals(BigDecimal.valueOf(1000), capturedTransaction.getAmount());
        assertEquals(Currency.RUB, capturedTransaction.getCurrency());
        assertEquals(TransactionStatus.IN_PROGRESS, capturedTransaction.getStatus());
    }

    @Test
    public void createYookassaPaymentResponse_ShouldThrowExceptionWhenClientBlocked() {
        testClient.setBlocked(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "+79001234567", null, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clientService.findByPhone("+79001234567")).thenReturn(testClient);

        ResponseEntity<YookassaPaymentResponse> responseEntity =
                new ResponseEntity<>(yookassaPaymentResponse, HttpStatus.OK);
        when(yookassaFeignClient.createPayment(any(HttpHeaders.class), any(YookassaPaymentRequest.class)))
                .thenReturn(responseEntity);

        assertThrows(PaymentServiceException.class, () -> {
            yookassaPaymentService.createYookassaPaymentResponse(createPaymentRequest);
        });
    }

    @Test
    public void getPaymentDetails_ShouldReturnResponseEntityWhenPaymentIdValid() {
        String paymentId = "123e4567-e89b-12d3-a456-426614174000";
        ResponseEntity<YookassaPaymentResponse> expectedResponse =
                new ResponseEntity<>(yookassaPaymentResponse, HttpStatus.OK);

        when(yookassaFeignClient.getPaymentDetails(any(HttpHeaders.class), eq(paymentId)))
                .thenReturn(expectedResponse);

        ResponseEntity<YookassaPaymentResponse> response = yookassaPaymentService.getPaymentDetails(paymentId);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void getPaymentDetails_ShouldThrowExceptionWhenPaymentIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            yookassaPaymentService.getPaymentDetails(null);
        });
    }
}