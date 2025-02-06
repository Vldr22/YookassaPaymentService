package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.model.request.ClientGenerateSmsCodeRequest;
import com.education.mypaymentservice.model.request.ClientRegistrationRequest;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.request.LoginEmployeeRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.TokenResponse;
import com.education.mypaymentservice.service.common.ClientBlockService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.security.JwtTokenService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import com.education.mypaymentservice.service.security.SmsCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final SmsCodeService smsCodeService;
    private final JwtTokenService jwtTokenService;
    private final ClientBlockService clientBlockService;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationCodeService registrationCodeService;
    private final ClientService clientService;

    public ClientResponse registeredClient (ClientRegistrationRequest request) {

        Client client = new Client(
                request.getName(),
                request.getSurname(),
                request.getMidname(),
                request.getPhone()
                );

        Client clientAdded = clientService.add(client);

        String fullClientName = client.getSurname() + " " + client.getName();
        if (client.getMidname() != null) {
            fullClientName += " " + client.getMidname();
        }
        return new ClientResponse(fullClientName,clientAdded.getPhone());
    }

    public EmployeeResponse registeredAdmin(EmployeeRegistrationRequest request) {
        if (employeeService.isFirstAdminSetupComplete()) {
            throw new PaymentServiceException("Администратор уже был создан", "email", request.getEmail());
        }

        if (!registrationCodeService.validateCode(request.getCode())) {
            throw new UnauthorizedException("Неверный код. Проверьте правильность введенных данных",
                    "Вы ввели код: ", request.getCode());
        }
        return employeeService.getRegisteredEmployeeResponse(request, Roles.ROLE_ADMIN);
    }

    public EmployeeResponse registeredEmployee(EmployeeRegistrationRequest request) {
        if (registrationCodeService.isValidateCode(request.getCode(), request.getEmail())) {
            registrationCodeService.changeCodeAsUsed(request.getCode());
            return employeeService.getRegisteredEmployeeResponse(request, Roles.ROLE_EMPLOYEE);
        } else {
            throw new UnauthorizedException("Неверный регистрационный код для email", "email", request.getEmail());
        }
    }

    public boolean isValidateEmployeePassword(LoginEmployeeRequest request) {
        Employee employee = employeeService.findEmployeeByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), employee.getPassword())) {
                throw new UnauthorizedException("Неверное имя пользователя или пароль", "email", request.email());
        }
        registrationCodeService.changeCodeAsUsed(employee.getEmail());
        return true;
    }

    public String sendSmsCode(String phone) {
        SmsCode smsCode = smsCodeService.create(phone);
        return smsCodeService.send(smsCode.getPhone());
    }

    public TokenResponse generateAndSendTokenForClient(ClientGenerateSmsCodeRequest request) {
        if (smsCodeService.isValidate(request.phone(), request.code())) {

            String validPhone = normalizeRussianPhoneNumber(request.phone());
            String token = jwtTokenService.generateToken(validPhone, Roles.ROLE_CLIENT);

            clientBlockService.unblock(normalizeRussianPhoneNumber(validPhone));
            smsCodeService.updateStatus(smsCodeService.findByPhone(validPhone), SmsCodeStatus.VERIFIED);

            return new TokenResponse(token);
        } else {
           throw new UnauthorizedException("Неверный Смс-код или телефон!", "phone", request.phone());
        }
    }

    public TokenResponse generateAndSendTokenForEmployee(LoginEmployeeRequest request) {
          if (isValidateEmployeePassword(request)) {
              String token = jwtTokenService.generateToken(request.email(), Roles.ROLE_EMPLOYEE);
              return new TokenResponse(token);
          } else {
              throw new UnauthorizedException("Неверный пароль или email!", "email", request.email());
          }
    }

    public TokenResponse generateAndSendTokenForAdmin(LoginEmployeeRequest request) {
        if (isValidateEmployeePassword(request)) {
            String token = jwtTokenService.generateToken(request.email(), Roles.ROLE_ADMIN);
            return new TokenResponse(token);
        } else {
            throw new UnauthorizedException("Неверный пароль или email!", "email", request.email());
        }
    }

    public void addTokenToCookie (String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("auth-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        //создал куки с установленным временем так. Не стал писать логику для забора данных для отдельно
        //каждой роли
        cookie.setMaxAge(86400);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
