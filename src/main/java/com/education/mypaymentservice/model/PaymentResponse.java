package com.education.mypaymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class PaymentResponse {

    private UUID id;
    private String status;
    private PaymentRequest.Amount amount;
    private String description;
    private Recipient recipient;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private Confirmation confirmation;
    private boolean test;
    private boolean paid;
    private boolean refundable;
    private Map<String, Object> metadata;

    public UUID getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public PaymentRequest.Amount getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public boolean isTest() {
        return test;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public static class Confirmation {
        private String type;

        @JsonProperty("return_url")
        private String returnUrl;

        @JsonProperty("confirmation_url")
        private String confirmationUrl;

        public String getType() {
            return type;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public String getConfirmationUrl() {
            return confirmationUrl;
        }


        @Override
        public String toString() {
            return "Confirmation{" +
                    "type='" + type + '\'' +
                    ", returnUrl='" + returnUrl + '\'' +
                    ", confirmationUrl='" + confirmationUrl + '\'' +
                    '}';
        }
    }


    public static class Recipient {
        @JsonProperty("account_id")
        private String accountId;

        @JsonProperty("gateway_id")
        private String gatewayId;

        public String getAccountId() {
            return accountId;
        }

        public String getGatewayId() {
            return gatewayId;
        }


        @Override
        public String toString() {
            return "Recipient{" +
                    "accountId='" + accountId + '\'' +
                    ", gatewayId='" + gatewayId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", recipient=" + recipient +
                ", createdAt=" + createdAt +
                ", confirmation=" + confirmation +
                ", test=" + test +
                ", paid=" + paid +
                ", refundable=" + refundable +
                ", metadata=" + metadata +
                '}';
    }
}
