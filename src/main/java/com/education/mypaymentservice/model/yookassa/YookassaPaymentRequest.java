package com.education.mypaymentservice.model.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YookassaPaymentRequest {

    private Amount amount;

    private Confirmation confirmation;

    private String description;

    private boolean capture;

    @JsonProperty("save_payment_method")
    private boolean savePaymentMethod;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    private Subscription subscription;

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("payment_method_data")
    private PaymentMethodData paymentMethodData;

}
