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

    public CardToken add(CardToken cardToken) {
        return cardTokenRepository.save(cardToken);
    }
}
