package com.education.mypaymentservice.model.yookassa;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodData {
    private String type;
    private CardData card;
}


