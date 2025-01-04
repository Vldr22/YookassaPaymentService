package com.education.mypaymentservice.model.response;

import com.education.mypaymentservice.model.enums.TransactionStatus;
import com.education.mypaymentservice.utils.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.ForClient.class)
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @JsonView(Views.ForClient.class)
    private BigDecimal amount;

    @JsonView(Views.ForClient.class)
    private Currency currency;

    @JsonView(Views.ForClient.class)
    private TransactionStatus status;

    private BigDecimal fee;

    private BigDecimal feePercent;

    ClientResponse clientResponse;

}
