package com.education.mypaymentservice.model.entity;

import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Sms_Codes")
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 4)
    private String code;

    @Column(nullable = false, updatable = false)
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
