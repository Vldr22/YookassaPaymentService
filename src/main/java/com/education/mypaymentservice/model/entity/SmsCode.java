package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Sms_Codes")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long smsId;

    @Column(nullable = false, length = 4)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column
    private LocalDateTime updateDate;

    private LocalDateTime expireTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SmsCodeStatus status;

    @Column(nullable = false)
    private String phone;

}
