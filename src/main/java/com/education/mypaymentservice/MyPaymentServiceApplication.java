package com.education.mypaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class MyPaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyPaymentServiceApplication.class, args);
    }
}
