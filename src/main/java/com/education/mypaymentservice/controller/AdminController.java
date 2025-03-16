package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.common.CommonResponse;
import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.model.request.EmailRequest;
import com.education.mypaymentservice.model.request.UpdateFeePercentRequest;
import com.education.mypaymentservice.model.response.RegistrationCodeResponse;
import com.education.mypaymentservice.service.common.AppSettingService;
import com.education.mypaymentservice.service.forController.EmployeeService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RegistrationCodeService registrationCodeService;
    private final EmployeeService employeeService;
    private final AppSettingService appSettingService;

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
    public CommonResponse<List<AppSetting>> getSettings() {
        List<AppSetting> appSetting = appSettingService.getAll();
        return CommonResponse.success(appSetting);
    }

    @PutMapping("/feePercent")
    public CommonResponse<String> updateFeePercent(@Valid @RequestBody UpdateFeePercentRequest request) {
            appSettingService.updateFeePercent(request);
            return CommonResponse.success("Настройки успешно обновлены!");
    }
}
