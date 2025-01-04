package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.model.request.AdminSetupRequest;
import com.education.mypaymentservice.model.request.ClientGenerateSmsCodeRequest;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.service.common.ClientBlockService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.security.JwtTokenService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import com.education.mypaymentservice.service.security.SmsCodeService;
import com.education.mypaymentservice.settings.AdminSetup;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final SmsCodeService smsCodeService;
    private final JwtTokenService jwtTokenService;
    private final ClientBlockService clientBlockService;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final AdminSetup firstAdminSetupService;
    private final RegistrationCodeService registrationCodeService;

    @Getter
    private final ClientService clientService;

    public ClientResponse registeredClient (Client client) {
        Client clientAdded = clientService.addClient(client);

        String fullClientName = client.getSurname() + " " + client.getName();
        if (client.getMidname() != null) {
            fullClientName += " " + client.getMidname();
        }


        ClientResponse clientResponse = new ClientResponse(fullClientName,clientAdded.getPhone());
        try {
            System.out.println(new ObjectMapper().writeValueAsString(clientResponse));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return clientResponse;
    }

    public String registeredAdmin(AdminSetupRequest request) {
        if (firstAdminSetupService.isFirstAdminSetupComplete()) {
            throw new UnauthorizedException("Администратор уже был создан", "email", request.getEmail());
        }

        if (firstAdminSetupService.validateSetupToken(request.getSetupToken())) {
            throw new UnauthorizedException("Неверный Setup-token", "setup-token", request.getSetupToken());
        }

        Employee admin = Employee.builder()
                .name(request.getName())
                .surname(request.getSurName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.ROLE_ADMIN)
                .build();

        employeeService.addAdmin(admin);
        firstAdminSetupService.invalidateSetupToken();
        return jwtTokenService.generateJWTTokenForAdmin(request.getEmail());
    }

    public EmployeeResponse registeredEmployee(EmployeeRegistrationRequest request) {
        if (registrationCodeService.isValidateCode(request.getCode(), request.getEmail())) {
            return employeeService.getRegisteredEmployeeResponse(request);
        } else {
            throw new PaymentServiceException("Неверный регистрационный код для email", "email", request.getEmail());
        }
    }


    public boolean isValidateEmployeePassword(EmployeeRegistrationRequest request) {
        Employee employee = employeeService.findEmployeeByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                throw new UnauthorizedException("Неверное имя пользователя или пароль", "email", request.getEmail());
        }
        registrationCodeService.changeCodeAsUsed(employee.getEmail());
        return true;
    }

    public String sendSmsCode(String phone) {
        SmsCode smsCode = smsCodeService.createSmsCode(phone);
        return smsCodeService.sendSmsCode(smsCode.getPhone());
    }

    public String generateAndSendTokenForClient(ClientGenerateSmsCodeRequest request) {
        if (smsCodeService.isValidateSmsCode(request.getPhone(), request.getCode())) {

            String validPhone = normalizeRussianPhoneNumber(request.getPhone());
            String token = jwtTokenService.generateJWTTokenForClient(validPhone);

            clientBlockService.unblockUser(normalizeRussianPhoneNumber(validPhone));
            smsCodeService.updateSmsSendStatus(smsCodeService.findSmsCode(validPhone), SmsCodeStatus.VERIFIED);
            return token;
        } else {
           throw new UnauthorizedException("Неверный Смс-код или телефон!", "phone", request.getPhone());
        }
    }

      public String generateAndSendTokenForEmployee(EmployeeRegistrationRequest request) {

          if (isValidateEmployeePassword(request)) {
              return jwtTokenService.generateJWTTokenForEmployee(request.getEmail());
          } else {
              throw new UnauthorizedException("Неверный пароль или email!", "email", request.getCode());
          }

    }
}
