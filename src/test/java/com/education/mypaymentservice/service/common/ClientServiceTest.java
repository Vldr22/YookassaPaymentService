package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.ClientRepository;
import com.education.mypaymentservice.service.model.TestModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        testClient = TestModelFactory.createTestClient();
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
                TestModelFactory.createTestClientWithArguments("+79991234455","Иван", "Иванов"),
                TestModelFactory.createTestClientWithArguments("+78001234567", "Василий", "Васильев"));

        when(clientRepository.findAll()).thenReturn(expectedClients);

        List<Client> result = clientService.findAllClients();

        assertEquals(expectedClients, result);
        assertEquals(expectedClients.size(), result.size());
        assertEquals(testClient.getPhone(), result.get(0).getPhone());
        verify(clientRepository).findAll();
    }
}
