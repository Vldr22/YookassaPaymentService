package com.education.mypaymentservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilterRequest {
    String phone;
    LocalDateTime startDate;
    LocalDateTime endDate;
    BigDecimal minAmount;
    BigDecimal maxAmount;
}
