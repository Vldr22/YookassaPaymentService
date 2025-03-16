package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.repository.SmsCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${sms.code.expiration.minutes}")
    private long smsCodeExpiration;

    public SmsCode create(String phone) {

        String verifyPhone = normalizeRussianPhoneNumber(phone);

        if (smsCodeRepository.findByPhone(verifyPhone) != null) {
            smsCodeRepository.delete(smsCodeRepository.findByPhone(verifyPhone));
        }

        SmsCode smsCode = new SmsCode();

        smsCode.setPhone(verifyPhone);
        smsCode.setCode(generate());
        smsCode.setCreateDate(LocalDateTime.now());
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(smsCodeExpiration));
        smsCode.setStatus(SmsCodeStatus.CREATED);

        Optional<SmsCode> smsCodeOptional = Optional.of(smsCodeRepository.save(smsCode));
        return smsCodeOptional.orElseThrow(() -> new PaymentServiceException(
                "Ошибка при создании СМС-КОДа для телефона: " + phone));
    }

    public boolean isExpired(SmsCode smsCode) {
        return LocalDateTime.now().isAfter(smsCode.getExpireTime());
    }

    public SmsCode findByPhone(String phone) {
        SmsCode smsCode = smsCodeRepository.findByPhone(normalizeRussianPhoneNumber(phone));
        if (isExpired(smsCode)) {
            expire(normalizeRussianPhoneNumber(phone));
            throw new PaymentServiceException("Срок действия СМС-КОДа для телефона: " + phone + " истёк");
        }
        return smsCode;
    }

    public void updateStatus(SmsCode smsCode, SmsCodeStatus status) {
        smsCode.setStatus(status);
        smsCode.setUpdateDate(LocalDateTime.now());
        smsCodeRepository.save(smsCode);
    }

    public String send(String phone) {
       SmsCode smsCode = smsCodeRepository.findByPhone(normalizeRussianPhoneNumber(phone));
       updateStatus(smsCode, SmsCodeStatus.SEND);
       return smsCode.getCode();
    }

    public String generate() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public boolean isValidate(String phone, String code) {
        SmsCode smsCode = findByPhone(normalizeRussianPhoneNumber(phone));

        if (isExpired(smsCode)) {
            throw new PaymentServiceException("Срок действия СМС-кода для телефона: " + phone + " истёк");
        }

        boolean isValid = code.equals(smsCode.getCode());
        if (isValid) {
            updateStatus(smsCode, SmsCodeStatus.VERIFIED);
        }
        return isValid;
    }

    public void expire(String phone) {
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
