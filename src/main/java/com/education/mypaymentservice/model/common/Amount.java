package com.education.mypaymentservice.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@Builder
public class Amount {
    private BigDecimal value;
    private Currency currency;
}
