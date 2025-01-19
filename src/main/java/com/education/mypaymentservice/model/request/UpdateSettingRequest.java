package com.education.mypaymentservice.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
public class UpdateSettingRequest {
    private BigDecimal feePercent;
    private long SecondsJwtTokenExpirationClient;
    private long SecondsJwtTokenExpirationEmployee;
    private long SecondsJwtTokenExpirationAdmin;
    private long MinutesExpireTimeSmsCode;
}
