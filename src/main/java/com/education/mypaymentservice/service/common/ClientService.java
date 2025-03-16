package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client add(Client client) {
        try {
            client.setPhone(normalizeRussianPhoneNumber(client.getPhone()));
            client.setBlocked(true);
            client.setRegistrationDate(LocalDateTime.now());

            return clientRepository.save(client);
        } catch (Exception e) {
            throw new PaymentServiceException("Ошибка при добавлении клиента c телефоном: "
                    + client.getPhone());
        }
    }

    public Client findByPhone(String phone) {
        String normalizedPhone = normalizeRussianPhoneNumber(phone);
        return clientRepository.findByPhone(normalizedPhone).orElseThrow(()
                -> new PaymentServiceException("Клиент с телефоном: " + phone + " не найден"));
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }
}
