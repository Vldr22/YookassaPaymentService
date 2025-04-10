package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClientGenerateSmsCodeRequest(@NotNull String phone, @Size(min = 4, max = 4) String code) {}
