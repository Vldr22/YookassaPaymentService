package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.settings.AppSettingSingleton;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.repository.SmsCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
@RequiredArgsConstructor
@Transactional
public class SmsCodeService {

    private final SmsCodeRepository smsCodeRepository;
    private final AppSettingSingleton appSettingSingleton;

    public SmsCode createSmsCode(String phone) {

        String verifyPhone = normalizeRussianPhoneNumber(phone);

        if (smsCodeRepository.findByPhone(verifyPhone) != null) {
            smsCodeRepository.delete(smsCodeRepository.findByPhone(verifyPhone));
        }

        SmsCode smsCode = SmsCode.builder()
                .phone(verifyPhone)
                .code(generateSmsCode())
                .createDate(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(appSettingSingleton.getAppSetting()
                        .getMinutesExpireTimeSmsCode()))
                .status(SmsCodeStatus.CREATED)
                .build();

        Optional<SmsCode> smsCodeOptional = Optional.of(smsCodeRepository.save(smsCode));
        return smsCodeOptional.orElseThrow(() -> new PaymentServiceException(
                "Ошибка при создании СМС-КОДа для телефона: " + phone));
    }

    public boolean isSmsCodeExpired(SmsCode smsCode) {
        return LocalDateTime.now().isAfter(smsCode.getExpireTime());
    }

    public SmsCode findSmsCode(String phone) {
        SmsCode smsCode = smsCodeRepository.findByPhone(normalizeRussianPhoneNumber(phone));
        if (isSmsCodeExpired(smsCode)) {
            expireSmsCode(normalizeRussianPhoneNumber(phone));
            throw new PaymentServiceException("Срок действия СМС-КОДа для телефона: " + phone + " истёк");
        }
        return smsCode;
    }

    public void updateSmsSendStatus(SmsCode smsCode, SmsCodeStatus status) {
        smsCode.setStatus(status);
        smsCode.setUpdateDate(LocalDateTime.now());
        smsCodeRepository.save(smsCode);
    }

    public String sendSmsCode(String phone) {
       SmsCode smsCode = smsCodeRepository.findByPhone(normalizeRussianPhoneNumber(phone));
       updateSmsSendStatus(smsCode, SmsCodeStatus.SEND);
       return smsCode.getCode();
    }

    public String generateSmsCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public boolean isValidateSmsCode(String phone, String code) {
        SmsCode smsCode = findSmsCode(normalizeRussianPhoneNumber(phone));

        if (isSmsCodeExpired(smsCode)) {
            throw new PaymentServiceException("Срок действия СМС-кода для телефона: " + phone + " истёк");
        }

        boolean isValid = code.equals(smsCode.getCode());
        if (isValid) {
            updateSmsSendStatus(smsCode, SmsCodeStatus.VERIFIED);
        }
        return isValid;
    }

    public void expireSmsCode(String phone) {
        SmsCode smsCode = smsCodeRepository.findByPhone(normalizeRussianPhoneNumber(phone));

        smsCode.setStatus(SmsCodeStatus.EXPIRED);
        smsCode.setUpdateDate(LocalDateTime.now());
        smsCodeRepository.save(smsCode);
    }

    public void removeExpiredAndVerifiedSmsCodes() {
        List<SmsCodeStatus> statusesToDelete = List.of(SmsCodeStatus.EXPIRED, SmsCodeStatus.VERIFIED);
        smsCodeRepository.deleteByStatuses(statusesToDelete);
    }

    public void changeExpiredSmsCodes() {
        smsCodeRepository.updateExpiredSmsCodes(
                List.of(SmsCodeStatus.CREATED, SmsCodeStatus.SEND),
                SmsCodeStatus.EXPIRED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
