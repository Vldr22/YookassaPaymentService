package com.education.mypaymentservice.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Recipient {
    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("gateway_id")
    private String gatewayId;
}
