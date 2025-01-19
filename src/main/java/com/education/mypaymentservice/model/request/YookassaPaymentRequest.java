package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.yookassa.Amount;
import com.education.mypaymentservice.model.yookassa.Confirmation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YookassaPaymentRequest {

    private Amount amount;

    private Confirmation confirmation;

    private String description;

    private boolean capture;

    @JsonProperty("save_payment_method")
    private boolean savePaymentMethod;

}
