package com.education.mypaymentservice.model.yookassa;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardData {
    private String number;
    private String expireMonth;
    private String expireYear;
    private String csc;
}
