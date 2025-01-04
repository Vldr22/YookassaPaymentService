package com.education.mypaymentservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "app_settings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppSetting {

    @Id
    @JsonIgnore
    private Integer id;

    private BigDecimal feePercent;

    private long SecondsJwtTokenExpirationClient;

    private long SecondsJwtTokenExpirationEmployee;

    private long SecondsJwtTokenExpirationAdmin;

    private long MinutesExpireTimeSmsCode;

}
