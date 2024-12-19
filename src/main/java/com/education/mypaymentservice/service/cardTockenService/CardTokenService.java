package com.education.mypaymentservice.service.cardTockenService;

import com.education.mypaymentservice.dto.CardToken;
import com.education.mypaymentservice.service.repository.CardTokenRepository;
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
        return optionalCardToken.orElseThrow(() -> new RuntimeException("Ошибка при добавлении токена!"));
    }
}
