package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.request.EmailRequest;
import com.education.mypaymentservice.model.request.UpdateSettingRequest;
import com.education.mypaymentservice.model.response.RegistrationCodeResponse;
import com.education.mypaymentservice.service.forController.EmployeeService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RegistrationCodeService registrationCodeService;
    private final EmployeeService employeeService;

    @PostMapping("/generate-employee-registration-code")
    public CommonResponse<RegistrationCodeResponse> generateEmployeeRegistrationCode(@RequestBody EmailRequest request) {
        RegistrationCodeResponse codeResponse = registrationCodeService.generateCode(request.getEmail());
        return CommonResponse.success(codeResponse);
    }

    @DeleteMapping("/cleanup-sms-codes")
    public CommonResponse<String> cleanupSmsCodes() {
        employeeService.removeExpiredAndVerifiedSmsCodes();
        return CommonResponse.success("Смс-кода успешно удалены!");
    }

    @GetMapping("/settings")
    public CommonResponse<AppSetting> getSettings() {
        AppSetting appSetting = employeeService.getAppSetting();
        return CommonResponse.success(appSetting);
    }

    @PutMapping("/settings")
    public CommonResponse<String> updateSettings(@RequestBody UpdateSettingRequest request) {
            employeeService.updateAppSetting(request);
            return CommonResponse.success("Настройки успешно обновлены!");
    }
}
