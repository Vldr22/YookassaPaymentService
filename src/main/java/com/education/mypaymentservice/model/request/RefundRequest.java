package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.yookassa.Amount;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundRequest {

    @JsonProperty("payment_id")
    private String paymentId;

    private Amount amount;

    private String description;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;
}
