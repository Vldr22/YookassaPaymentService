package com.education.mypaymentservice.model.yookassa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Confirmation {
    public String type;

    @JsonProperty("return_url")
    public String returnUrl;

    @JsonProperty("confirmation_url")
    public String confirmationUrl;

}
