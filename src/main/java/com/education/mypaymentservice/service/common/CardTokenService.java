package com.education.mypaymentservice.service.common;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.CardToken;
import com.education.mypaymentservice.repository.CardTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardTokenService {

    private final CardTokenRepository cardTokenRepository;

    public CardTokenService(CardTokenRepository cardTokenRepository) {
        this.cardTokenRepository = cardTokenRepository;
    }

    public CardToken addCardToken(CardToken cardToken) {
        Optional<CardToken> optionalCardToken = Optional.of(cardTokenRepository.save(cardToken));
        return optionalCardToken.orElseThrow(() -> new PaymentServiceException("Ошибка при добавлении токена!",
                "client_id", cardToken.getClient().getId().toString()));
    }
}
