package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.model.entity.AppSetting;
import com.education.mypaymentservice.service.forController.EmployeeService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import feign.Headers;
import feign.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RegistrationCodeService registrationCodeService;
    private final EmployeeService employeeService;

    @PostMapping("/generate-employee-registration-code")
    @Headers("Content-Type: application/json")
    public ResponseEntity<String> generateEmployeeRegistrationCode(@Param String email) {
        String code = registrationCodeService.generateCode(email);
        return ResponseEntity.ok("Регистрационный код успешно сгенерирован: " + code);
    }

    @DeleteMapping("/cleanup-sms-codes")
    public ResponseEntity<String> cleanupSmsCodes() {
        employeeService.removeExpiredAndVerifiedSmsCodes();
        return ResponseEntity.ok("Смс кода успешно удалены!");
    }

    @GetMapping("/getSettings")
    public AppSetting getSettings() {
        return employeeService.getAppSetting();
    }

    @PostMapping("/updateSettings")
    public ResponseEntity<String> updateSettings(@RequestBody AppSetting newSettings) {
        employeeService.updateAppSetting(newSettings);
        return ResponseEntity.ok("Настройки успешно обновлены!");
    }
}
