package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;
    private String normalizePhone;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setName("Test Name");
        testClient.setSurname("Test Surname");
        testClient.setMidname("Test Midname");
        testClient.setPhone("+79001234567");

        normalizePhone = normalizeRussianPhoneNumber(testClient.getPhone());
    }
    @Test
    public void add_WithValidPhone_ShouldReturnClient() {
        when(clientRepository.save(any(Client.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        Client result = clientService.add(testClient);
        assertEquals("79001234567", result.getPhone());
        verify(clientRepository).save(testClient);
    }

    @Test
    public void add_WIthInvalidPhone_ShouldThrowException() {
        testClient.setPhone("12345");

        assertThatThrownBy(() -> clientService.add(testClient))
                .isInstanceOf(PaymentServiceException.class);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void findByPhone_WhenFind_ShouldReturnClient() {
        when(clientRepository.findByPhone(normalizeRussianPhoneNumber(testClient.getPhone()))).thenReturn(Optional.of(testClient));

        Client expectedClient = testClient;
        Client result = clientService.findByPhone(expectedClient.getPhone());

        assertEquals(testClient, result);
        assertEquals(testClient.getPhone(), result.getPhone());
        verify(clientRepository).findByPhone(normalizeRussianPhoneNumber(testClient.getPhone()));
    }

    @Test
    public void findByPhone_WithNoFind_ShouldThrowException() {
        when(clientRepository.findByPhone(normalizePhone)).thenReturn(Optional.empty());

        assertThrows(PaymentServiceException.class, () ->
                clientService.findByPhone(normalizePhone)
        );
        verify(clientRepository).findByPhone(normalizePhone);
    }

    @Test
    public void findAllClients_WhenClientsPresent_ShouldReturnAllClients() {
        List<Client> expectedClients = List.of(
                testClient,
                createTestClient("+79991234455","Иван", "Иванов"),
                createTestClient("+78001234567", "Василий", "Васильев"));

        when(clientRepository.findAll()).thenReturn(expectedClients);

        List<Client> result = clientService.findAllClients();

        assertEquals(expectedClients, result);
        assertEquals(expectedClients.size(), result.size());
        assertEquals(testClient.getPhone(), result.get(0).getPhone());
        verify(clientRepository).findAll();
    }

    private Client createTestClient(String phone, String name, String surname) {
        Client client = new Client();
        client.setPhone(phone);
        client.setName(name);
        client.setSurname(surname);
        client.setRole(Roles.ROLE_CLIENT);
        client.setBlocked(true);
        client.setRegistrationDate(LocalDateTime.now());
        return client;
    }
}
