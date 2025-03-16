package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientBlockServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientBlockService clientBlockService;

    private Client testClient;
    private String normalizedPhone;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setName("Test Name");
        testClient.setSurname("Test Surname");
        testClient.setMidname("Test Midname");
        testClient.setPhone("+79001234567");
        testClient.setBlocked(true);

        normalizedPhone = normalizeRussianPhoneNumber(testClient.getPhone());
    }

    @Test
    public void unblock_WhenClientPresent_ShouldChangeBlockedToFalse() {
        when(clientRepository.findByPhone(normalizedPhone)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        clientBlockService.unblock(normalizedPhone);

        assertFalse(testClient.isBlocked());
        verify(clientRepository).findByPhone(normalizedPhone);
        verify(clientRepository).save(testClient);
    }

    @Test
    public void unblock_WhenUserDoesNotExist_ShouldNeverSave() {
        when(clientRepository.findByPhone(normalizedPhone)).thenReturn(Optional.empty());

        clientBlockService.unblock(normalizedPhone);

        verify(clientRepository).findByPhone(normalizedPhone);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void isBlocked_WhenUserDoesNotExist_ShouldReturnFalse() {
        testClient.setBlocked(false);

        when(clientRepository.findByPhone(normalizedPhone)).thenReturn(Optional.of(testClient));

        boolean result = clientBlockService.isBlocked(normalizedPhone);

        assertFalse(result);
        verify(clientRepository).findByPhone(normalizedPhone);
    }

    @Test
    public void block_WhenClientPresent_ShouldChangeBlockedToTrue() {
        testClient.setBlocked(false);

        when(clientRepository.findByPhone(normalizedPhone)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        clientBlockService.block(normalizedPhone);

        assertTrue(testClient.isBlocked());
        verify(clientRepository).findByPhone(normalizedPhone);
        verify(clientRepository).save(testClient);
    }
}
