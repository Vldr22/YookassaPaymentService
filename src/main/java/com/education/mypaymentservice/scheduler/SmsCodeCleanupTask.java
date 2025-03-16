package com.education.mypaymentservice.scheduler;

import com.education.mypaymentservice.service.security.SmsCodeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SmsCodeCleanupTask {

    private final SmsCodeService smsCodeService;

    public SmsCodeCleanupTask(SmsCodeService smsCodeService) {
        this.smsCodeService = smsCodeService;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES,
            fixedRateString = "${sms.code.expiration.minutes}",
            initialDelayString = "${sms.code.expiration.minutes}")
    public void cleanupTask() {
        try {
            smsCodeService.removeExpiredAndVerifiedSmsCodes();
            log.info("Список SMS-кодов очищен!");
        } catch (Exception e) {
            log.error("Список SMS-кодов НЕ ОЧИЩЕН", e);
            log.trace(e.getMessage(), e);
        }
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES,
            fixedRateString = "${sms.code.expiration.minutes}",
            initialDelayString = "${sms.code.expiration.minutes}")
    public void updateStatusTask() {
        try {
            smsCodeService.changeExpiredSmsCodes();
            log.info("Статус истекших СМС-кодов обновлен!");
        } catch (Exception e) {
            log.error("Статус истекших СМС-кодов НЕ ОБНОВИЛСЯ", e);
            log.trace(e.getMessage(), e);
        }
    }
}



