package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.repository.CardTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CardTokenService {

    private final CardTokenRepository cardTokenRepository;

    public CardTokenService(CardTokenRepository cardTokenRepository) {
        this.cardTokenRepository = cardTokenRepository;
    }

    public CardToken add(CardToken cardToken) {
        return cardTokenRepository.save(cardToken);
    }

    public String findValueTokenByClientId(UUID client_id) {
        Optional<CardToken> cardToken = cardTokenRepository.findByClientId(client_id);
        return cardToken.map(CardToken::getToken).orElse(null);
    }

    public CardToken findTokenByClientId(UUID client_id) {
        Optional<CardToken> cardToken = cardTokenRepository.findByClientId(client_id);
        return cardToken.orElseThrow(()-> new PaymentServiceException("Токен не найден с id клиента: " + client_id));
    }
}
