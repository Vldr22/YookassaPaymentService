package com.education.mypaymentservice.service.forController;

import com.education.mypaymentservice.exception.UnauthorizedException;
import com.education.mypaymentservice.model.entity.Client;
import com.education.mypaymentservice.model.entity.Employee;
import com.education.mypaymentservice.model.entity.SmsCode;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.enums.SmsCodeStatus;
import com.education.mypaymentservice.model.request.ClientGenerateSmsCodeRequest;
import com.education.mypaymentservice.model.request.ClientRegistrationRequest;
import com.education.mypaymentservice.model.request.EmployeeRegistrationRequest;
import com.education.mypaymentservice.model.request.LoginEmployeeRequest;
import com.education.mypaymentservice.model.response.ClientResponse;
import com.education.mypaymentservice.model.response.EmployeeResponse;
import com.education.mypaymentservice.model.response.TokenResponse;
import com.education.mypaymentservice.service.common.ClientBlockService;
import com.education.mypaymentservice.service.common.ClientService;
import com.education.mypaymentservice.service.model.TestModelFactory;
import com.education.mypaymentservice.service.security.JwtTokenService;
import com.education.mypaymentservice.service.security.RegistrationCodeService;
import com.education.mypaymentservice.service.security.SmsCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ClientService clientService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private RegistrationCodeService registrationCodeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private SmsCodeService smsCodeService;

    @Mock
    private ClientBlockService clientBlockService;

    @InjectMocks
    private AuthenticationService authenticationService;

    Client testClient;
    EmployeeRegistrationRequest employeeRegistrationRequest;
    String validCode = "1234";

    @BeforeEach
    void setUp() {
        testClient = TestModelFactory.createTestClient();

        employeeRegistrationRequest = new EmployeeRegistrationRequest();
        employeeRegistrationRequest.setEmail("testEmloyee@mail.com");
        employeeRegistrationRequest.setName("Иван");
        employeeRegistrationRequest.setSurName("Иванов");
        employeeRegistrationRequest.setMidName("Иванович");
        employeeRegistrationRequest.setPassword("12345678");
        employeeRegistrationRequest.setCode(validCode);
    }

    @Test
    public void clientRegistration_WithValidRequest_ShouldReturnClientResponse() {

        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
                .name(testClient.getName())
                .surname(testClient.getSurname())
                .midname(testClient.getMidname())
                .phone(testClient.getPhone())
                .build();

        when(clientService.add(any(Client.class))).thenReturn(testClient);

        ClientResponse response = authenticationService.registeredClient(request);

        assertEquals("Иванов Иван Иванович", response.getFullName());
        assertEquals("+79001234567", response.getPhone());
    }

    @Test
    public void registeredAdmin_WithValidRequest_ShouldReturnRegisteredEmployeeResponse() {

        EmployeeResponse expectedResponse = new EmployeeResponse(
                "Иванов Иван Иванович",
                "testEmloyee@mail.com"
        );

        when(employeeService.isFirstAdminSetupComplete()).thenReturn(false);
        when(registrationCodeService.validateCode(validCode)).thenReturn(true);
        when(employeeService.getRegisteredEmployeeResponse(any(EmployeeRegistrationRequest.class), eq(Roles.ROLE_ADMIN)))
                .thenReturn(expectedResponse);


        EmployeeResponse response = authenticationService.registeredAdmin(employeeRegistrationRequest);

        assertEquals("Иванов Иван Иванович", response.fullName());
        assertEquals("testEmloyee@mail.com", response.email());
        assertEquals(expectedResponse, response);

        verify(employeeService).isFirstAdminSetupComplete();
        verify(registrationCodeService).validateCode(validCode);
        verify(employeeService).getRegisteredEmployeeResponse(employeeRegistrationRequest, Roles.ROLE_ADMIN);
    }

    @Test
    public void registeredEmployee_WithValidRequest_ShouldReturnRegisteredEmployeeResponse() {

        EmployeeResponse expectedResponse = new EmployeeResponse(
                "Иванов Иван Иванович",
                "testEmloyee@mail.com"
        );

        when(registrationCodeService.isValidateCode(validCode, "testEmloyee@mail.com")).thenReturn(true);
        when(employeeService.getRegisteredEmployeeResponse(any(EmployeeRegistrationRequest.class), eq(Roles.ROLE_EMPLOYEE)))
                .thenReturn(expectedResponse);

        EmployeeResponse response = authenticationService.registeredEmployee(employeeRegistrationRequest);

        assertEquals("Иванов Иван Иванович", response.fullName());
        assertEquals("testEmloyee@mail.com", response.email());
        verify(employeeService).getRegisteredEmployeeResponse(employeeRegistrationRequest, Roles.ROLE_EMPLOYEE);
    }

    @Test
    public void registeredEmployee_WithInvalidCode_ShouldThrowUnauthorizedException() {

        when(registrationCodeService.isValidateCode(validCode, "testEmloyee@mail.com")).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.registeredEmployee(employeeRegistrationRequest);
        });

        verify(registrationCodeService).isValidateCode(validCode, "testEmloyee@mail.com");
        verify(employeeService, never()).getRegisteredEmployeeResponse(any(), any());
    }

    @Test
    void isValidateEmployeePassword_WithInvalidPassword_ShouldThrowException() {

        String password = "wrongPassword";
        Employee employee = new Employee();
        employee.setEmail(employeeRegistrationRequest.getEmail());
        employee.setPassword(employeeRegistrationRequest.getPassword());


        when(employeeService.findEmployeeByEmail(employee.getEmail())).thenReturn(employee);
        when(passwordEncoder.matches(password, employee.getPassword())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.isValidateEmployeePassword(
                    new LoginEmployeeRequest(employee.getEmail(), password));
        });

        assertTrue(exception.getMessage().contains("Неверное имя пользователя или пароль"));
        verify(registrationCodeService, never()).changeCodeAsUsed(anyString());
    }

    @Test
    void isValidateEmployeePassword_WithValidPassword_ShouldReturnTrue() {

        Employee employee = new Employee();
        employee.setEmail(employeeRegistrationRequest.getEmail());
        employee.setPassword(employeeRegistrationRequest.getPassword());

        when(employeeService.findEmployeeByEmail(employee.getEmail())).thenReturn(employee);
        when(passwordEncoder.matches("12345678", employee.getPassword())).thenReturn(true);

        boolean result = authenticationService.isValidateEmployeePassword(
                new LoginEmployeeRequest(employee.getEmail(), employee.getPassword()));

        assertTrue(result);
        verify(registrationCodeService).changeCodeAsUsed("testEmloyee@mail.com");
    }

    @Test
    public void generateAndSendTokenForClient_WithValidCode_ShouldReturnToken() {

        String phone = "+79001234567";
        String normalizedPhone = "79001234567";
        String smsCode = "123456";
        String expectedToken = "jwt.token.example";

        ClientGenerateSmsCodeRequest request = new ClientGenerateSmsCodeRequest(phone, smsCode);
        SmsCode smsCodeEntity = new SmsCode();

        when(smsCodeService.isValidate(phone, smsCode)).thenReturn(true);
        when(jwtTokenService.generateToken(normalizedPhone, Roles.ROLE_CLIENT)).thenReturn(expectedToken);
        when(smsCodeService.findByPhone(normalizedPhone)).thenReturn(smsCodeEntity);

        TokenResponse response = authenticationService.generateAndSendTokenForClient(request);

        assertEquals(expectedToken, response.token());

        verify(smsCodeService).isValidate(phone, smsCode);
        verify(jwtTokenService).generateToken(normalizedPhone, Roles.ROLE_CLIENT);
        verify(clientBlockService).unblock(normalizedPhone);
        verify(smsCodeService).updateStatus(smsCodeEntity, SmsCodeStatus.VERIFIED);
    }

    @Test
    public void generateAndSendTokenForEmployee_WithValidCredentials_ShouldReturnToken() {
        String email = employeeRegistrationRequest.getEmail();
        String password = employeeRegistrationRequest.getPassword();
        String expectedToken = "employee.jwt.token";

        LoginEmployeeRequest request = new LoginEmployeeRequest(email, password);

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(employeeRegistrationRequest.getPassword());

        when(employeeService.findEmployeeByEmail(email)).thenReturn(employee);
        when(passwordEncoder.matches(password, employee.getPassword())).thenReturn(true);
        when(jwtTokenService.generateToken(email, Roles.ROLE_EMPLOYEE)).thenReturn(expectedToken);

        TokenResponse response = authenticationService.generateAndSendTokenForEmployee(request);

        assertEquals(expectedToken, response.token());
        verify(jwtTokenService).generateToken(email, Roles.ROLE_EMPLOYEE);
        verify(registrationCodeService).changeCodeAsUsed(email);
    }

    @Test
    public void generateAndSendTokenForEmployee_WithInvalidCredentials_ShouldThrowException() {
        String email = employeeRegistrationRequest.getEmail();
        String password = "invalidPassword";

        LoginEmployeeRequest request = new LoginEmployeeRequest(email, password);

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(employeeRegistrationRequest.getPassword());

        when(employeeService.findEmployeeByEmail(email)).thenReturn(employee);
        when(passwordEncoder.matches(password, employee.getPassword())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.generateAndSendTokenForEmployee(request);
        });

        assertTrue(exception.getMessage().contains("Неверное имя пользователя или пароль"));
        verify(jwtTokenService, never()).generateToken(anyString(), any(Roles.class));
        verify(registrationCodeService, never()).changeCodeAsUsed(anyString());
    }

    @Test
    public void generateAndSendTokenForAdmin_WithValidCredentials_ShouldReturnToken() {
        String email = employeeRegistrationRequest.getEmail();
        String password = employeeRegistrationRequest.getPassword();
        String expectedToken = "admin.jwt.token";

        LoginEmployeeRequest request = new LoginEmployeeRequest(email, password);

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(employeeRegistrationRequest.getPassword());

        when(employeeService.findEmployeeByEmail(email)).thenReturn(employee);
        when(passwordEncoder.matches(password, employee.getPassword())).thenReturn(true);
        when(jwtTokenService.generateToken(email, Roles.ROLE_ADMIN)).thenReturn(expectedToken);

        TokenResponse response = authenticationService.generateAndSendTokenForAdmin(request);

        assertEquals(expectedToken, response.token());
        verify(jwtTokenService).generateToken(email, Roles.ROLE_ADMIN);
        verify(registrationCodeService).changeCodeAsUsed(email);
    }

    @Test
    public void generateAndSendTokenForAdmin_WithInvalidCredentials_ShouldThrowException() {
        String email = "admin@example.com";
        String password = "invalidPassword";

        LoginEmployeeRequest request = new LoginEmployeeRequest(email, password);

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword("hashedPassword");

        when(employeeService.findEmployeeByEmail(email)).thenReturn(employee);
        when(passwordEncoder.matches(password, employee.getPassword())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.generateAndSendTokenForAdmin(request);
        });

        assertTrue(exception.getMessage().contains("Неверное имя пользователя или пароль"));
        verify(jwtTokenService, never()).generateToken(anyString(), any(Roles.class));
        verify(registrationCodeService, never()).changeCodeAsUsed(anyString());
    }

    @Test
    public void addTokenToCookie_ShouldAddCookieToResponse() {
        String token = "test.jwt.token";
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        authenticationService.addTokenToCookie(token, response);

        verify(response).addCookie(cookieCaptor.capture());

        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("auth-token", capturedCookie.getName());
        assertEquals(token, capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertTrue(capturedCookie.getSecure());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(86400, capturedCookie.getMaxAge());
        assertEquals("Strict", capturedCookie.getAttribute("SameSite"));
    }
}






