package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.request.*;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.TokenResponse;
import com.education.mypaymentservice.service.forController.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration-client")
    public CommonResponse<ClientResponse> registrationClient(@Valid @RequestBody ClientRegistrationRequest request) {
        ClientResponse registeredClient = authenticationService.registeredClient(request);
        return CommonResponse.success(registeredClient);
    }

    @PostMapping("/registration-employee")
    public CommonResponse<EmployeeResponse> registrationEmployee(@Valid @RequestBody EmployeeRegistrationRequest request) {
        EmployeeResponse registeredEmployee = authenticationService.registeredEmployee(request);
        return CommonResponse.success(registeredEmployee);
    }

    @PostMapping("/registration-admin")
    public CommonResponse<EmployeeResponse> registrationAdmin(@Valid @RequestBody EmployeeRegistrationRequest request) {
        EmployeeResponse employeeResponse = authenticationService.registeredAdmin(request);
        return CommonResponse.success(employeeResponse);
    }

    @PostMapping("/generated-sms-code")
    public CommonResponse<SmsCodeRequest> requestSmsCode(@Valid @RequestBody PhoneRequest phone) {
        String code = authenticationService.sendSmsCode(String.valueOf(phone));
        SmsCodeRequest codeResponse = new SmsCodeRequest(code);
        return CommonResponse.success(codeResponse);
    }

    @PostMapping("/login-client")
    public CommonResponse<TokenResponse> verifySmsCode(@Valid @RequestBody ClientGenerateSmsCodeRequest request,
                                                       HttpServletResponse httpServletResponse) {
        TokenResponse tokenResponse = authenticationService.generateAndSendTokenForClient(request);
        authenticationService.addTokenToCookie(tokenResponse.token(), httpServletResponse);
        return CommonResponse.success(tokenResponse);
    }

    @PostMapping("/login-employee")
    public CommonResponse<TokenResponse> loginEmployee(@Valid @RequestBody LoginEmployeeRequest request,
                                                       HttpServletResponse httpServletResponse) {
        TokenResponse tokenResponse = authenticationService.generateAndSendTokenForEmployee(request);
        authenticationService.addTokenToCookie(tokenResponse.token(), httpServletResponse);
        return CommonResponse.success(tokenResponse);
    }

    @PostMapping("/login-admin")
    public CommonResponse<TokenResponse> requestSmsCode(@Valid @RequestBody LoginEmployeeRequest request,
                                                        HttpServletResponse httpServletResponse) {
        TokenResponse tokenResponse = authenticationService.generateAndSendTokenForAdmin(request);
        authenticationService.addTokenToCookie(tokenResponse.token(), httpServletResponse);
        return CommonResponse.success(tokenResponse);
    }

}

