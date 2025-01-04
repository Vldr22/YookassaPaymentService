package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.common.Amount;
import lombok.Data;

@Data
public class CreatePaymentRequest {
    private Amount amount;
    private String returnUrl;
    private String description;
}
