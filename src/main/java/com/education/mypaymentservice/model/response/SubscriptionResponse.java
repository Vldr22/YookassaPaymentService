package com.education.mypaymentservice.model.response;

import com.education.mypaymentservice.model.enums.SubscriptionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionResponse {
    private String message;

    @JsonProperty("confirmation_url")
    public String confirmationUrl;

    private SubscriptionType subscriptionType;

    private int periodInMonths;

}
