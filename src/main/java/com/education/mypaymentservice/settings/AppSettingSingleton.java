package com.education.mypaymentservice.settings;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.repository.AppSettingRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
public class AppSettingSingleton {

    private final AppSetting appSetting;
    private final AppSettingRepository appSettingRepository;

    public AppSettingSingleton(@Value("${feePercent}") BigDecimal feePercent,
                               @Value("${jwt.token.expiration.client.second}") long jwtTokenClientExpiration,
                               @Value("${jwt.token.expiration.employee.second}") long jwtTokenEmployeeExpiration,
                               @Value("${jwt.token.expiration.admin.second}") long jwtTokenAdminExpiration,
                               @Value("${sms.code.expiration.minutes}") long smsCodeExpiration,
                               AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;

        this.appSetting = appSettingRepository.findById(1).orElseGet(() -> {
            AppSetting defaultSetting = new AppSetting(
                    1,
                    feePercent,
                    jwtTokenClientExpiration,
                    jwtTokenEmployeeExpiration,
                    jwtTokenAdminExpiration,
                    smsCodeExpiration
            );

            appSettingRepository.save(defaultSetting);
            return defaultSetting;
        });
    }

    public void updateAppSetting(AppSetting newSettings) {
        this.appSetting.setFeePercent(newSettings.getFeePercent());
        this.appSetting.setSecondsJwtTokenExpirationClient(newSettings.getSecondsJwtTokenExpirationClient());
        this.appSetting.setSecondsJwtTokenExpirationAdmin(newSettings.getSecondsJwtTokenExpirationAdmin());
        this.appSetting.setSecondsJwtTokenExpirationEmployee(newSettings.getSecondsJwtTokenExpirationEmployee());
        this.appSetting.setMinutesExpireTimeSmsCode(newSettings.getMinutesExpireTimeSmsCode());

        appSettingRepository.save(this.appSetting);
    }
}
