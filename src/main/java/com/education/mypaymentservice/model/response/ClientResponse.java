package com.education.mypaymentservice.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientResponse {

    private String fullName;
    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;

    private Boolean blocked;

    public ClientResponse(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
        this.registrationDate = null;
        this.blocked = null;
    }
}
