package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.repository.SmsCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmsCodeServiceTest {

    @Mock
    private SmsCodeRepository smsCodeRepository;

    @InjectMocks
    private SmsCodeService smsCodeService;

    private SmsCode testSmsCode;
    private String testPhone;
    private String normalizedPhone;
    private String testCode;

    @BeforeEach
    void setUp() {

        testPhone = "+79001234567";
        testCode = "1234";

        normalizedPhone = normalizeRussianPhoneNumber(testPhone);

        testSmsCode = new SmsCode();
        testSmsCode.setPhone(normalizedPhone);
        testSmsCode.setCode(testCode);
        testSmsCode.setCreateDate(LocalDateTime.now());
        testSmsCode.setExpireTime(LocalDateTime.now().plusMinutes(10));
        testSmsCode.setStatus(SmsCodeStatus.CREATED);
    }

    @Test
    public void create_WithNewPhone_ShouldCreateAndSaveSmsCode() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(null);
        when(smsCodeRepository.save(any(SmsCode.class))).thenReturn(testSmsCode);

        SmsCode result = smsCodeService.create(testPhone);

        assertEquals(normalizedPhone, result.getPhone());
        assertEquals(testSmsCode.getCode(), result.getCode());
        assertEquals(SmsCodeStatus.CREATED, result.getStatus());
        verify(smsCodeRepository).save(any(SmsCode.class));
    }

    @Test
    public void create_WithExistingPhone_ShouldDeleteOldAndCreateNew() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);
        when(smsCodeRepository.save(any(SmsCode.class))).thenReturn(testSmsCode);

        SmsCode result = smsCodeService.create(testPhone);

        assertEquals(normalizedPhone, result.getPhone());
        assertEquals(SmsCodeStatus.CREATED, result.getStatus());
        verify(smsCodeRepository).delete(testSmsCode);
        verify(smsCodeRepository).save(any(SmsCode.class));
    }

    @Test
    public void isExpired_WithExpiredCode_ShouldReturnTrue() {
        SmsCode expiredCode = new SmsCode();
        expiredCode.setExpireTime(LocalDateTime.now().minusMinutes(1));

        boolean result = smsCodeService.isExpired(expiredCode);

        assertTrue(result);
    }

    @Test
    public void isExpired_WithValidCode_ShouldReturnFalse() {
        SmsCode validCode = new SmsCode();
        validCode.setExpireTime(LocalDateTime.now().plusMinutes(1));

        boolean result = smsCodeService.isExpired(validCode);

        assertFalse(result);
    }

    @Test
    public void findByPhone_WithValidCode_ShouldReturnSmsCode() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);

        SmsCode result = smsCodeService.findByPhone(testPhone);

        assertEquals(testSmsCode, result);
    }

    @Test
    public void findByPhone_WithExpiredCode_ShouldThrowException() {
        SmsCode expiredCode = new SmsCode();
        expiredCode.setPhone(normalizedPhone);
        expiredCode.setExpireTime(LocalDateTime.now().minusMinutes(1));

        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(expiredCode);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            smsCodeService.findByPhone(testPhone);
        });

        assertTrue(exception.getMessage().contains("истёк"));
        verify(smsCodeRepository).save(any(SmsCode.class));
    }

    @Test
    public void updateStatus_ShouldUpdateStatusAndSaveCode() {
        SmsCodeStatus newStatus = SmsCodeStatus.SEND;

        smsCodeService.updateStatus(testSmsCode, newStatus);

        assertEquals(newStatus, testSmsCode.getStatus());
        assertNotNull(testSmsCode.getUpdateDate());
        verify(smsCodeRepository).save(testSmsCode);
    }

    @Test
    public void send_ShouldUpdateStatusAndReturnCode() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);

        String result = smsCodeService.send(testPhone);

        assertEquals(testCode, result);
        verify(smsCodeRepository).save(any(SmsCode.class));
    }

    @Test
    public void generate_ShouldReturnFourDigitCode() {
        String result = smsCodeService.generate();

        assertEquals(4, result.length());
        assertTrue(result.matches("\\d{4}"));
    }

    @Test
    public void isValidate_WithValidCode_ShouldReturnTrue() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);

        boolean result = smsCodeService.isValidate(testPhone, testCode);

        assertTrue(result);
        verify(smsCodeRepository).save(any(SmsCode.class));
    }

    @Test
    public void isValidate_WithInvalidCode_ShouldReturnFalse() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);

        boolean result = smsCodeService.isValidate(testPhone, "5678");

        assertFalse(result);
        verify(smsCodeRepository, never()).save(any(SmsCode.class));
    }

    @Test
    public void isValidate_WithExpiredCode_ShouldThrowException() {
        SmsCode expiredCode = new SmsCode();
        expiredCode.setPhone(normalizedPhone);
        expiredCode.setExpireTime(LocalDateTime.now().minusMinutes(1));

        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(expiredCode);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            smsCodeService.isValidate(testPhone, testCode);
        });

        assertTrue(exception.getMessage().contains("истёк"));
    }

    @Test
    public void expire_ShouldUpdateStatusToExpired() {
        when(smsCodeRepository.findByPhone(normalizedPhone)).thenReturn(testSmsCode);

        smsCodeService.expire(testPhone);

        assertEquals(SmsCodeStatus.EXPIRED, testSmsCode.getStatus());
        verify(smsCodeRepository).save(testSmsCode);
    }

    @Test
    public void removeExpiredAndVerifiedSmsCodes_ShouldDeleteCodesByStatuses() {
        smsCodeService.removeExpiredAndVerifiedSmsCodes();
        verify(smsCodeRepository).deleteByStatuses(List.of(SmsCodeStatus.EXPIRED, SmsCodeStatus.VERIFIED));
    }

    @Test
    public void changeExpiredSmsCodes_ShouldUpdateStatusForExpiredCodes() {
        smsCodeService.changeExpiredSmsCodes();

        verify(smsCodeRepository).updateExpiredSmsCodes(
                eq(List.of(SmsCodeStatus.CREATED, SmsCodeStatus.SEND)),
                eq(SmsCodeStatus.EXPIRED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }
}