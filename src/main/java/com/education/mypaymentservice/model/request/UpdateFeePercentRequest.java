package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateFeePercentRequest {
    @NotNull
    @Positive
    private BigDecimal feePercent;
}
