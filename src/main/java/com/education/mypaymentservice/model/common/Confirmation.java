package com.education.mypaymentservice.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Confirmation {
    public String type;

    @JsonProperty("return_url")
    public String returnUrl;

    @JsonProperty("confirmation_url")
    public String confirmationUrl;

    public Confirmation(String type, String returnUrl) {
        this.type = type;
        this.returnUrl = returnUrl;
    }


}
