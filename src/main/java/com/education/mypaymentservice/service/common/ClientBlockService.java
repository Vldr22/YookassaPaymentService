package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientBlockService {

    private final ClientRepository clientRepository;

    public ClientBlockService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean isUserBlocked(String phone) {
        return clientRepository.findByPhone(phone)
                .map(Client::isBlocked)
                .orElse(true);
    }

    public void unblockUser(String phone) {
        clientRepository.findByPhone(phone)
                .ifPresent(client -> {
                    client.setBlocked(false);
                    clientRepository.save(client);
                });
    }

    public void blockUser(String phone) {
        clientRepository.findByPhone(phone)
                .ifPresent(client -> {
                    client.setBlocked(true);
                    clientRepository.save(client);
                });
    }
}
