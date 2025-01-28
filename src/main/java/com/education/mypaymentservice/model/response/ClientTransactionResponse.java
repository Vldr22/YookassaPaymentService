package com.education.mypaymentservice.model.response;

import com.education.mypaymentservice.model.enums.Currency;
import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ClientTransactionResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private BigDecimal amount;
    private Currency currency;
    private TransactionStatus status;
}
