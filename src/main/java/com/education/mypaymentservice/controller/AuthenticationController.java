package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.request.AdminSetupRequest;
import com.education.mypaymentservice.model.request.ClientGenerateSmsCodeRequest;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.service.forController.AuthenticationService;
import feign.Headers;
import feign.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration-client")
    public ResponseEntity<ClientResponse> registrationClient(@RequestBody Client client) {
        ClientResponse registeredClient = authenticationService.registeredClient(client);
        return ResponseEntity.ok(registeredClient);
    }

    @PostMapping("/registration-employee")
    public ResponseEntity<EmployeeResponse> registrationEmployee(@RequestBody EmployeeRegistrationRequest request) {
        EmployeeResponse registeredEmployee = authenticationService.registeredEmployee(request);
        return ResponseEntity.ok(registeredEmployee);
    }

    @PostMapping("/login")
    @Headers("Content-Type: application/json")
    public ResponseEntity<String> requestSmsCode(@Param String phone) {
        String smsCode = authenticationService.sendSmsCode(phone);
        return ResponseEntity.ok("СМС код: " + smsCode);
    }

    @PostMapping("/verify-sms-code")
    public ResponseEntity<?> verifySmsCode(@RequestBody ClientGenerateSmsCodeRequest request) {
        String token = authenticationService.generateAndSendTokenForClient(request);
        return ResponseEntity.ok("Авторизация клиента прошла успешно: " + token);
    }
    @PostMapping("/login-employee")
    public ResponseEntity<String> loginEmployee(@RequestBody EmployeeRegistrationRequest request) {
        String token = authenticationService.generateAndSendTokenForEmployee(request);
        return ResponseEntity.ok("Авторизация сотрудника прошла успешно: " + token);
    }

    @PostMapping("/setup-admin")
    public ResponseEntity<?> setupFirstAdmin(@RequestBody AdminSetupRequest request) {
        String token = authenticationService.registeredAdmin(request);
        return ResponseEntity.ok("Аккаунт админа создан успешно: " + token);
    }
}

