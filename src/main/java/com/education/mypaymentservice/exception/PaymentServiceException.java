package com.education.mypaymentservice.exception;

import lombok.Getter;

@Getter
public class PaymentServiceException extends RuntimeException {

    private String requiredPermission;
    private String resource;

    public PaymentServiceException(String message, String requiredPermission, String resource) {
        super(message);
        this.requiredPermission = requiredPermission;
        this.resource = resource;
    }

    public PaymentServiceException(String message) {
        super(message);
    }
}
