package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.NotNull;

public record SmsCodeRequest(@NotNull String code) {
}
