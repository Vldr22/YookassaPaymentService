package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.yookassa.Amount;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundRequest {

    @NotNull
    @JsonProperty("payment_id")
    private String paymentId;

    @NotNull
    private Amount amount;

    @NotNull
    private String description;

    @NotNull
    @JsonProperty("payment_method_id")
    private String paymentMethodId;
}
