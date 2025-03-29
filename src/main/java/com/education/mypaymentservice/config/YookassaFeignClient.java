package com.education.mypaymentservice.config;

import com.education.mypaymentservice.model.yookassa.YookassaPaymentRequest;
import com.education.mypaymentservice.model.yookassa.YookassaPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "yookassa-api", url = "${yookassa.api.url}")
public interface YookassaFeignClient {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<YookassaPaymentResponse> createPayment(
            @RequestHeader HttpHeaders headers,
            @RequestBody YookassaPaymentRequest request
    );

    @GetMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<YookassaPaymentResponse> getPaymentDetails(
            @RequestHeader HttpHeaders headers,
            @PathVariable String paymentId
    );
}
