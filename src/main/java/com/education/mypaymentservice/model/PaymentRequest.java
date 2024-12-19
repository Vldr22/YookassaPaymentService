package com.education.mypaymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Currency;

public class PaymentRequest {

    private Amount amount;
    private Confirmation confirmation;
    private String description;
    private boolean capture;

    @JsonProperty("save_payment_method")
    private boolean savePaymentMethod;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCapture() {
        return capture;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    public boolean isSavePaymentMethod() {
        return savePaymentMethod;
    }

    public void setSavePaymentMethod(boolean savePaymentMethod) {
        this.savePaymentMethod = savePaymentMethod;
    }

    public static class Amount {
        private BigDecimal value;
        private Currency currency;

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(Currency currency) {
            this.currency = currency;
        }

        @Override
        public String toString() {
            return "Amount{" +
                    "value=" + value +
                    ", currency=" + currency +
                    '}';
        }
    }

    public static class Confirmation {
        private String type;

        @JsonProperty("return_url")
        private String returnUrl;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        @Override
        public String toString() {
            return "Confirmation{" +
                    "type='" + type + '\'' +
                    ", returnUrl='" + returnUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "amount=" + amount +
                ", confirmation=" + confirmation +
                ", description='" + description + '\'' +
                ", capture=" + capture +
                ", savePaymentMethod=" + savePaymentMethod +
                '}';
    }
}
