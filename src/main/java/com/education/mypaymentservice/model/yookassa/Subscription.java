package com.education.mypaymentservice.model.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Subscription {

    private String interval;

    private Integer period;

    @JsonProperty("start_date")
    private String startDate;
}
