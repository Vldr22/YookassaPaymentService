package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
public class ClientBlockService {

    private final ClientRepository clientRepository;

    public ClientBlockService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean isBlocked(String phone) {
        return clientRepository.findByPhone(normalizeRussianPhoneNumber(phone))
                .map(Client::isBlocked)
                .orElse(false);
    }

    public void unblock(String phone) {
        clientRepository.findByPhone(normalizeRussianPhoneNumber(phone))
                .ifPresent(client -> {
                    if (client.isBlocked()) {
                        client.setBlocked(false);
                        clientRepository.save(client);
                    }
                });
    }

    public void block(String phone) {
        clientRepository.findByPhone(normalizeRussianPhoneNumber(phone))
                .ifPresent(client -> {
                    client.setBlocked(true);
                    clientRepository.save(client);
                });
    }
}
