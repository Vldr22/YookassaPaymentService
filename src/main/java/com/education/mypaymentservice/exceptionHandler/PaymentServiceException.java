package com.education.mypaymentservice.exceptionHandler;

public class PaymentServiceException extends RuntimeException {
    public PaymentServiceException(String message) {
        super(message);
    }
}
