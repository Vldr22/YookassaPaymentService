package com.education.mypaymentservice.controller;

import com.education.mypaymentservice.dto.Client;
import com.education.mypaymentservice.service.clientService.ClientBlockService;
import com.education.mypaymentservice.service.clientService.ClientService;
import com.education.mypaymentservice.service.securityService.JwtTokenService;
import com.education.mypaymentservice.service.securityService.SmsCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final SmsCodeService smsCodeService;
    private final JwtTokenService jwtTokenService;
    private final ClientService clientService;
    private final ClientBlockService clientBlockService;

    public AuthenticationController(SmsCodeService smsCodeService, JwtTokenService jwtTokenService, ClientService clientService, ClientBlockService clientBlockService) {
        this.smsCodeService = smsCodeService;
        this.jwtTokenService = jwtTokenService;
        this.clientService = clientService;
        this.clientBlockService = clientBlockService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> requestSmsCode(@RequestParam String name,
                                       @RequestParam String surname,
                                       @RequestParam String midname,
                                       @RequestParam (defaultValue = "true") boolean blocked,
                                       @RequestParam String phone) {

        clientService.addClient(new Client(name,surname,midname, blocked,phone));
        String smsCode = smsCodeService.generateSmsCode(phone);
        return ResponseEntity.ok("СМС код: " + smsCode);
    }

    @PostMapping("/verify-sms")
    public ResponseEntity<?> verifySmsCode(
            @RequestParam String phone,
            @RequestParam String code) {
        if (smsCodeService.isValidateSmsCode(phone, code)) {
            String token = jwtTokenService.generateJWTToken(phone);

            smsCodeService.removeSmsCode(phone);

            clientBlockService.unblockUser(phone);
            return ResponseEntity.ok().body(token);
        } else {
            return ResponseEntity.status(401).body("Неверный смс код! Авторизация не выполнена");
        }
    }
}
