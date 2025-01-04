package com.education.mypaymentservice.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyToStringConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return (currency != null) ? currency.getCurrencyCode() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String currencyCode) {
        return (currencyCode != null && !currencyCode.isEmpty()) ? Currency.getInstance(currencyCode) : null;
    }
}
