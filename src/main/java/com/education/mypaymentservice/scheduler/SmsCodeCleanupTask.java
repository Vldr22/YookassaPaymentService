package com.education.mypaymentservice.scheduler;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.service.security.SmsCodeService;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsCodeCleanupTask {

    private final SmsCodeService smsCodeService;
    private final TaskScheduler taskScheduler;
    private final AppSettingSingleton appSettingSingleton;

    private ScheduledFuture<?> cleanupTask;
    private ScheduledFuture<?> updateStatusTask;

    @PostConstruct
    public void init() {
        scheduleCleanupTask();
        scheduleUpdateStatusTask();
    }

    private void scheduleCleanupTask() {
            if (cleanupTask != null) {
                cleanupTask.cancel(false);
            }

            cleanupTask = taskScheduler.schedule(
                    () -> {
                        try {
                            smsCodeService.removeExpiredAndVerifiedSmsCodes();
                            log.info("Список SMS-кодов очищен. Время очистки установлено как: {} минут",
                                    appSettingSingleton.getAppSetting().getMinutesExpireTimeSmsCode());
                        } catch (Exception e) {
                            log.error("Список SMS-кодов НЕ ОЧИЩЕН", e);
                            log.trace(e.getMessage(), e);
                        }
                    },
                    triggerContext -> {
                        AppSetting freshSettings = appSettingSingleton.getAppSetting();
                        Instant lastExecution = triggerContext.lastScheduledExecution();

                        if (lastExecution == null) {
                            return Instant.now().plusMillis(freshSettings.getMinutesExpireTimeSmsCode() * 60000);
                        }

                        return lastExecution.plusMillis(freshSettings.getMinutesExpireTimeSmsCode() * 60000);
                    }
            );
    }

    private void scheduleUpdateStatusTask() {
        if (updateStatusTask != null) {
            updateStatusTask.cancel(false);
        }

        updateStatusTask = taskScheduler.schedule(
                    () -> {
                        try {
                            smsCodeService.changeExpiredSmsCodes();
                            log.info("Статус истекших СМС-кодов обновлен. Время обновления установлено как: {} минут",
                                    appSettingSingleton.getAppSetting().getMinutesExpireTimeSmsCode());
                        } catch (Exception e) {
                            log.error("Статус истекших СМС-кодов НЕ ОБНОВИЛСЯ", e);
                            log.trace(e.getMessage(), e);
                        }
                    },
                triggerContext -> {
                        AppSetting freshSettings = appSettingSingleton.getAppSetting();
                        Instant lastExecution = triggerContext.lastScheduledExecution();

                    if (lastExecution == null) {
                        return Instant.now().plusMillis(freshSettings.getMinutesExpireTimeSmsCode() * 60000);
                    }

                    return lastExecution.plusMillis(freshSettings.getMinutesExpireTimeSmsCode() * 60000);
                }
            );
        }
    }



