package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ClientRegistrationRequest {

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @Size(min = 2, max = 50)
    private String surname;

    @Size(min = 2, max = 50)
    private String midname;

    @NotNull
    private String phone;
}
