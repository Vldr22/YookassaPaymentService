package com.education.mypaymentservice.service.clientService;

import com.education.mypaymentservice.dto.Client;
import com.education.mypaymentservice.service.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client addClient(Client client) {
        Optional<Client> savedClient = Optional.of(clientRepository.save(client));
        return savedClient.orElseThrow(() -> new RuntimeException("Ошибка при добавлении клиента!"));
    }

    public Client findClientById(UUID id) {
         return clientRepository.findById(id).orElseThrow(()
                 -> new RuntimeException("Клиент с id: " + id + " не найден"));
    }

    public Client findClientByPhone(String phone) {
        return clientRepository.findByPhone(phone).orElseThrow(()
                -> new RuntimeException("Клиент с таким телефоном не найден"));
    }
}
