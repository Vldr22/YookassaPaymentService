package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailRequest {
    @NotNull
    @Email
    private String email;
}
