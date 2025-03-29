package com.education.mypaymentservice.model.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentMethod {

    private String type;

    private String id;

    private boolean saved;
    private String status;
    private String title;

    @JsonProperty("account_number")
    private String accountNumber;

}
