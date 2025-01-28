package com.education.mypaymentservice.model.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateSettingRequest {
    private BigDecimal feePercent;
    private long secondsJwtTokenExpirationClient;
    private long secondsJwtTokenExpirationEmployee;
    private long secondsJwtTokenExpirationAdmin;
    private long minutesExpireTimeSmsCode;
}
