package com.education.mypaymentservice.dto.convertor;

import com.education.mypaymentservice.exceptionHandler.PaymentServiceException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

@Converter(autoApply = true)
public class BigDecimalToLongConverter implements AttributeConverter<BigDecimal, Long> {

    private static final int SCALE = 2;

    @Override
    public Long convertToDatabaseColumn(BigDecimal attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("BigDecimal не может быть равен null");
        }
        if (attribute.compareTo(BigDecimal.ZERO) < 0){
            throw new PaymentServiceException("BigDecimal не может быть меньше нуля");
        }

        return attribute.movePointRight(SCALE).longValueExact();
    }

    @Override
    public BigDecimal convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
           throw new IllegalArgumentException("Long не может быть равен null");
        }
        if (dbData < 0){
            throw new PaymentServiceException("Long не может быть меньше нуля");
        }
        return BigDecimal.valueOf(dbData).movePointLeft(SCALE);
    }

}
