package com.education.mypaymentservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_settings")
public class AppSetting {

    @Id
    private Integer id;

    private BigDecimal feePercent;

    private long SecondsJwtTokenExpirationClient;

    private long SecondsJwtTokenExpirationEmployee;

    private long SecondsJwtTokenExpirationAdmin;

    private long MinutesExpireTimeSmsCode;

}
