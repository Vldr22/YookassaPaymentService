package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.yookassa.Amount;

public record CreatePaymentRequest(Amount amount, String returnUrl, String description) {
}
