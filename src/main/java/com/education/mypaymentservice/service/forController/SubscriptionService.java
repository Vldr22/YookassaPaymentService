package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Subscription;
import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.SubscriptionType;
import com.education.mypaymentservice.model.request.CreatePaymentRequest;
import com.education.mypaymentservice.model.yookassa.Amount;
import com.education.mypaymentservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public CreatePaymentRequest createPaymentRequest(String description) {
        return new CreatePaymentRequest(
                new Amount(BigDecimal.valueOf(100), Currency.RUB),
                "http:12345",
                description);
    }

    public void addSubscriptionToDatabase(Client client, SubscriptionType subscriptionType,
                                           LocalDate startDate, LocalDate endDate) {

        Subscription subscription = new Subscription();
        subscription.setClient(client);
        subscription.setSubscriptionType(subscriptionType);
        subscription.setStartTime(startDate);
        subscription.setEndTime(endDate);
        subscription.setActive(false);

        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getAllNotActiveSubscription() {
       return subscriptionRepository.findAllByActiveNot(false);
    }

    public void deleteSubscription(Subscription subscription) {
        subscriptionRepository.delete(subscription);
    }

    public Subscription getSubscriptionByClient_Id(UUID id) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findByClientId(id);
        return subscriptionOptional.orElse(null);
    }

    public void updateSubscriptionActive(Client client, boolean isActive) {
       Optional<Subscription> subscriptionOptional = subscriptionRepository.findByClientId(client.getId());
       subscriptionOptional.ifPresent(subscription -> {
           subscription.setActive(isActive);
           subscriptionRepository.save(subscription);
       });
    }

    public void updateSubscriptionInDatabase(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

}
