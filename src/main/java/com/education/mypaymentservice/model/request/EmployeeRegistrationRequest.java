package com.education.mypaymentservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRegistrationRequest {

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String midName;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    @NotNull
    private String code;

}
