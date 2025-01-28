package com.education.mypaymentservice.utils;

import org.springframework.stereotype.Component;

@Component
public class NormalizeUtils {

    public static String normalizeRussianPhoneNumber(String phone) {
        String normalized = phone.trim().replaceAll("\\D", "");

        if (normalized.startsWith("8")) {
            normalized = "7" + normalized.substring(1);
        }

        if (!normalized.startsWith("7")) {
            normalized = "7" + normalized;
        }

        if (normalized.length() != 11) {
            throw new IllegalArgumentException("Некорректный российский номер телефона: " + phone);
        }
        return normalized;
    }

    public static String castToFullName(String name, String surname, String midname) {
        String fullClientName = surname + " " + name;
        if (midname != null) {
            fullClientName += " " + midname;
        }
        return fullClientName;
    }
}
