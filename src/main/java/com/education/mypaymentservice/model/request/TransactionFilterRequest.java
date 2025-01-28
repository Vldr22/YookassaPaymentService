package com.education.mypaymentservice.model.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionFilterRequest(String phone, LocalDateTime startDate, LocalDateTime endDate,
                                       BigDecimal minAmount, BigDecimal maxAmount) {
}
