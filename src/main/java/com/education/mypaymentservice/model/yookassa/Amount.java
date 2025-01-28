package com.education.mypaymentservice.model.yookassa;

import com.education.mypaymentservice.model.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class Amount {
    private BigDecimal value;
    private Currency currency;
}
