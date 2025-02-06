package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setName("Test Name");
        testClient.setSurname("Test Surname");
        testClient.setMidname("Test Midname");
        testClient.setPhone("+79001234567");
    }

    @Test
    public void addClient_WithValidPhone() {
        when(clientRepository.save(any(Client.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        Client result = clientService.addClient(testClient);
        assertEquals("79001234567", result.getPhone());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    public void addClient_InvalidPhone() {
        testClient.setPhone("12345");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.addClient(testClient);
        });

        assertEquals("Некорректный российский номер телефона: 12345", exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void addClient_WithInvalidName() {
        // Arrange
        testClient.setName("A");

        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> clientService.addClient(testClient)
        );

        // Проверяем сообщение об ошибке
        assertEquals("Ошибка при добавлении клиента c телефоном: " + testClient.getPhone(),
                exception.getMessage());

        // Проверяем, что save не вызывался
        verify(clientRepository, never()).save(any(Client.class));
    }




}
