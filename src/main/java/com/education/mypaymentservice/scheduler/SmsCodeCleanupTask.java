package com.education.mypaymentservice.scheduler;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.service.security.SmsCodeService;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsCodeCleanupTask implements InitializingBean {

    private final SmsCodeService smsCodeService;
    private final TaskScheduler taskScheduler;
    private final AppSettingSingleton appSettingSingleton;

    private ScheduledFuture<?> cleanupTask;
    private ScheduledFuture<?> updateStatusTask;

    @Override
    public void afterPropertiesSet() {
        scheduleCleanupTask();
        scheduleUpdateStatusTask();
    }

    private void scheduleCleanupTask() {

        AppSetting appSetting = appSettingSingleton.getAppSetting();

        if (cleanupTask != null) {
            cleanupTask.cancel(false);
        }

        cleanupTask = taskScheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        smsCodeService.removeExpiredAndVerifiedSmsCodes();
                        log.info("Список SMS-кодов очищен. Время очистки установлено как: {} минут",
                                appSetting.getMinutesExpireTimeSmsCode());
                    } catch (Exception e) {
                        log.error("Список SMS-кодов НЕ ОЧИЩЕН", e);
                        log.trace(e.getMessage(), e);
                    }
                },
                appSetting.getMinutesExpireTimeSmsCode()*60000
        );
    }

    private void scheduleUpdateStatusTask() {

        AppSetting appSetting = appSettingSingleton.getAppSetting();

        if (updateStatusTask != null) {
            updateStatusTask.cancel(false);
        }

        updateStatusTask = taskScheduler.scheduleAtFixedRate(
                    () -> {
                        try {
                            smsCodeService.changeExpiredSmsCodes();
                            log.info("Статус истекших СМС-кодов обновлен. Время обновления установлено как: {} минут",
                                   appSetting.getMinutesExpireTimeSmsCode());
                        } catch (Exception e) {
                            log.error("Статус истекших СМС-кодов НЕ ОБНОВИЛСЯ", e);
                            log.trace(e.getMessage(), e);
                        }
                    },
                appSetting.getMinutesExpireTimeSmsCode()*60000);
        }
    }



