package com.education.mypaymentservice.repository;

import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    SmsCode findByPhone(String phone);

    @Modifying
    @Query("DELETE FROM SmsCode s WHERE s.status IN (:statuses)")
    void deleteByStatuses(@Param("statuses") List<SmsCodeStatus> statuses);

    @Modifying
    @Query("UPDATE SmsCode s SET s.status = :newStatus, s.updateDate = :updateDate " +
            "WHERE s.status IN :currentStatuses AND s.expireTime < :now")
    void updateExpiredSmsCodes(
            @Param("currentStatuses") List<SmsCodeStatus> currentStatuses,
            @Param("newStatus") SmsCodeStatus newStatus,
            @Param("updateDate") LocalDateTime updateDate,
            @Param("now") LocalDateTime now
    );

}
