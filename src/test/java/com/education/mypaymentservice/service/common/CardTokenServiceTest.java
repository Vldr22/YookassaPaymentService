package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.CardTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardTokenServiceTest {

    @Mock
    private CardTokenRepository cardTokenRepository;

    @InjectMocks
    private CardTokenService cardTokenService;

    private CardToken testCardToken;

    @BeforeEach
    void setUp() {
        Client testClient = new Client();
        testClient.setName("Test Name");
        testClient.setSurname("Test Surname");
        testClient.setMidname("Test Midname");
        testClient.setPhone("+79001234567");

        testCardToken = new CardToken();
        testCardToken.setToken(UUID.randomUUID().toString());
        testCardToken.setClient(testClient);
    }

    @Test
    public void add_ValidCardToken_ReturnCardToken() {
        when(cardTokenRepository.save(any(CardToken.class))).thenReturn(testCardToken);

        CardToken result = cardTokenService.add(testCardToken);

        assertEquals(result.getToken(), testCardToken.getToken());
        verify(cardTokenRepository).save(testCardToken);
    }

    @Test
    public void add_WhenSaveFails_ShouldThrowException() {
        when(cardTokenRepository.save(any(CardToken.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> cardTokenService.add(testCardToken));
        verify(cardTokenRepository).save(testCardToken);
    }
}
