package com.education.mypaymentservice.model.request;


import com.education.mypaymentservice.model.enums.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubscriptionRequest {
    private int periodInMonths;
    private SubscriptionType subscriptionType = SubscriptionType.DEFAULT;
}
