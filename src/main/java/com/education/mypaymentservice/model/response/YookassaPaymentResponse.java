package com.education.mypaymentservice.model.response;

import com.education.mypaymentservice.model.common.Amount;
import com.education.mypaymentservice.model.common.Confirmation;
import com.education.mypaymentservice.model.common.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class YookassaPaymentResponse {

    private UUID id;
    private String status;
    private Amount amount;
    private String description;
    private Recipient recipient;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private Confirmation confirmation;
    private boolean test;
    private boolean paid;
    private boolean refundable;
    private Map<String, Object> metadata;
}
