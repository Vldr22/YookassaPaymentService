package com.education.mypaymentservice.model.response;

import com.education.mypaymentservice.model.yookassa.Amount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RefundResponse {
    private String id;
    private Amount amount;
    private String status;
    private LocalDateTime createdAt;
}
