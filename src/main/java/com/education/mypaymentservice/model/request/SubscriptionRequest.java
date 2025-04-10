package com.education.mypaymentservice.model.request;


import com.education.mypaymentservice.model.enums.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubscriptionRequest {
    @NotNull
    @Positive
    private int periodInMonths;
    private SubscriptionType subscriptionType = SubscriptionType.DEFAULT;
}
