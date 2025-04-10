package com.education.mypaymentservice.model.request;

import com.education.mypaymentservice.model.yookassa.Amount;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(@NotNull Amount amount, String returnUrl, String description) {
}
