package com.education.mypaymentservice.service.securityService;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsCodeService {
    private final Map<String, String> testSmsCodes = new ConcurrentHashMap<>();

    public String generateSmsCode(String phoneNumber) {
        String code = String.format("%04d", new Random().nextInt(10000));
        testSmsCodes.put(phoneNumber, code);
        return code;
    }

    public boolean isValidateSmsCode(String phoneNumber, String code) {
        return code.equals(testSmsCodes.get(phoneNumber));
    }

    public void removeSmsCode(String phoneNumber) {
        testSmsCodes.remove(phoneNumber);
    }
}
