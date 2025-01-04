package com.education.mypaymentservice.model.request;

import lombok.Data;

@Data
public class ClientGenerateSmsCodeRequest {
    private String phone;
    private String code;
}
