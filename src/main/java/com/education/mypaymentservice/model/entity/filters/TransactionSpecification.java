package com.education.mypaymentservice.model.entity.filters;

import com.education.mypaymentservice.model.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> byClientPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                phone != null ? criteriaBuilder.equal(root.get("client").get("phone"), phone) : null;
    }

    public static Specification<Transaction> byCreateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("createDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), endDate);
            }
            return null;
        };
    }

    public static Specification<Transaction> byAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount != null && maxAmount != null) {
                return criteriaBuilder.between(root.get("amount"), minAmount, maxAmount);
            } else if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount);
            } else if (maxAmount != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount);
            }
            return null;
        };
    }

}
