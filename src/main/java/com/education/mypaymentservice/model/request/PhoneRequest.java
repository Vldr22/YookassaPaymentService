package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.NotNull;

public record PhoneRequest(@NotNull String phone) {
}
