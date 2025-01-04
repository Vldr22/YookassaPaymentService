package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminSetupRequest {

    @NotNull
    private String name;

    @NotNull
    private String surName;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    @NotNull
    private String setupToken;
}
