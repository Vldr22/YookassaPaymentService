package com.education.mypaymentservice.model.yookassa;

import com.education.mypaymentservice.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class Amount {
    @NotNull
    @Positive
    private BigDecimal value;
    private Currency currency;
}
