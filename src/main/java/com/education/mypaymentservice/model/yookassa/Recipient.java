package com.education.mypaymentservice.model.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Recipient {
    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("gateway_id")
    private String gatewayId;
}
