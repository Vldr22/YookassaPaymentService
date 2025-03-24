package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.exception.PaymentServiceException;
import com.education.mypaymentservice.model.entity.RegistrationCode;
import com.education.mypaymentservice.model.enums.Roles;
import com.education.mypaymentservice.model.response.RegistrationCodeResponse;
import com.education.mypaymentservice.repository.RegistrationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationCodeServiceTest {

    @Mock
    private RegistrationCodeRepository registrationCodeRepository;

    @Mock
    private Environment environment;

    @InjectMocks
    private RegistrationCodeService registrationCodeService;

    private RegistrationCode testRegistrationCode;
    private String testEmail;
    private String testAdminEmail;
    private String testCode;

    @BeforeEach
    void setUp() {
        testEmail = "user@example.com";
        testAdminEmail = "admin@example.com";
        testCode = "abc123def456ghi789jkl";

        testRegistrationCode = RegistrationCode.builder()
                .code(testCode)
                .email(testEmail)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .role(Roles.ROLE_EMPLOYEE)
                .build();

        ReflectionTestUtils.setField(registrationCodeService, "codeExpirationHours", 24);
    }

    @Test
    public void generateCode_ForEmployee_ShouldCreateEmployeeCode() {
        when(registrationCodeRepository.save(any(RegistrationCode.class))).thenReturn(testRegistrationCode);

        RegistrationCodeResponse response = registrationCodeService.generateCode(testEmail);

        assertNotNull(response);
        assertNotNull(response.registrationCode());
        assertEquals(20, response.registrationCode().length());
        verify(registrationCodeRepository).save(any(RegistrationCode.class));
    }

    @Test
    public void generateCode_ForAdminWithEmail_ShouldCreateAdminCode() {
        when(environment.getProperty("app.admin.email")).thenReturn(testAdminEmail);
        when(registrationCodeRepository.save(any(RegistrationCode.class))).thenReturn(testRegistrationCode);

        RegistrationCodeResponse response = registrationCodeService.generateCode(testAdminEmail);

        assertNotNull(response);
        assertNotNull(response.registrationCode());
        verify(registrationCodeRepository).save(argThat(code ->
                code.getRole() == Roles.ROLE_ADMIN &&
                        code.getEmail().equals(testAdminEmail)
        ));
    }

    @Test
    public void findRegistrationCodeByEmail_WithPresentEmail_ShouldReturnCode() {
        when(registrationCodeRepository.findByEmail(testEmail)).thenReturn(Optional.of(testRegistrationCode));

        RegistrationCode result = registrationCodeService.findRegistrationCodeByEmail(testEmail);

        assertEquals(testRegistrationCode, result);
    }

    @Test
    public void findRegistrationCodeByEmail_WithNonPresentEmail_ShouldThrowException() {
        when(registrationCodeRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            registrationCodeService.findRegistrationCodeByEmail(testEmail);
        });

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    public void generateRandomCode_ShouldReturnCodeWithLength20() {
        String randomCode = ReflectionTestUtils.invokeMethod(registrationCodeService, "generateRandomCode");

        assertNotNull(randomCode);
        assertEquals(20, randomCode.length());
    }

    @Test
    public void isValidateCode_WithValidActiveCode_ShouldReturnTrue() {
        when(registrationCodeRepository.findByCodeAndEmail(testCode, testEmail))
                .thenReturn(Optional.of(testRegistrationCode));

        boolean result = registrationCodeService.isValidateCode(testCode, testEmail);

        assertTrue(result);
    }

    @Test
    public void isValidateCode_WithUsedCode_ShouldReturnFalse() {
        RegistrationCode usedCode = RegistrationCode.builder()
                .code(testCode)
                .email(testEmail)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(true)
                .role(Roles.ROLE_EMPLOYEE)
                .build();

        when(registrationCodeRepository.findByCodeAndEmail(testCode, testEmail))
                .thenReturn(Optional.of(usedCode));

        boolean result = registrationCodeService.isValidateCode(testCode, testEmail);

        assertFalse(result);
    }

    @Test
    public void isValidateCode_WithExpiredCode_ShouldReturnFalse() {
        RegistrationCode expiredCode = RegistrationCode.builder()
                .code(testCode)
                .email(testEmail)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .used(false)
                .role(Roles.ROLE_EMPLOYEE)
                .build();

        when(registrationCodeRepository.findByCodeAndEmail(testCode, testEmail))
                .thenReturn(Optional.of(expiredCode));

        boolean result = registrationCodeService.isValidateCode(testCode, testEmail);

        assertFalse(result);
    }

    @Test
    public void isValidateCode_WithNonExistingCode_ShouldReturnFalse() {
        when(registrationCodeRepository.findByCodeAndEmail(testCode, testEmail))
                .thenReturn(Optional.empty());

        boolean result = registrationCodeService.isValidateCode(testCode, testEmail);

        assertFalse(result);
    }

    @Test
    public void changeCodeAsUsed_WithExistingCode_ShouldMarkAsUsed() {
        when(registrationCodeRepository.findByCode(testCode)).thenReturn(Optional.of(testRegistrationCode));

        registrationCodeService.changeCodeAsUsed(testCode);

        assertTrue(testRegistrationCode.isUsed());
        verify(registrationCodeRepository).save(testRegistrationCode);
    }

    @Test
    public void changeCodeAsUsed_WithNonExistingCode_ShouldDoNothing() {
        when(registrationCodeRepository.findByCode(testCode)).thenReturn(Optional.empty());

        registrationCodeService.changeCodeAsUsed(testCode);

        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    public void initialize_WithNoAdminCodes_ShouldGenerateAdminCode() {
        when(registrationCodeRepository.countByRole(Roles.ROLE_ADMIN)).thenReturn(0);
        when(environment.getProperty("app.admin.email")).thenReturn(testAdminEmail);
        when(registrationCodeRepository.save(any(RegistrationCode.class))).thenReturn(testRegistrationCode);

        registrationCodeService.initialize();

        verify(registrationCodeRepository).save(any(RegistrationCode.class));
    }

    @Test
    public void initialize_WithExistingAdminCodes_ShouldNotGenerateAdminCode() {
        when(registrationCodeRepository.countByRole(Roles.ROLE_ADMIN)).thenReturn(1);

        registrationCodeService.initialize();

        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }

    @Test
    public void validateCode_WithValidAdminCode_ShouldReturnTrueAndMarkUsed() {
        RegistrationCode adminCode = RegistrationCode.builder()
                .code(testCode)
                .email(testAdminEmail)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .role(Roles.ROLE_ADMIN)
                .build();

        when(environment.getProperty("app.admin.email")).thenReturn(testAdminEmail);
        when(registrationCodeRepository.findByEmail(testAdminEmail)).thenReturn(Optional.of(adminCode));
        when(registrationCodeRepository.findByCode(testCode)).thenReturn(Optional.of(adminCode));

        boolean result = registrationCodeService.validateCode(testCode);

        assertTrue(result);
        verify(registrationCodeRepository).save(any(RegistrationCode.class));
    }

    @Test
    public void validateCode_WithInvalidAdminCode_ShouldReturnFalse() {
        RegistrationCode adminCode = RegistrationCode.builder()
                .code("differentCode")
                .email(testAdminEmail)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .role(Roles.ROLE_ADMIN)
                .build();

        when(environment.getProperty("app.admin.email")).thenReturn(testAdminEmail);
        when(registrationCodeRepository.findByEmail(testAdminEmail)).thenReturn(Optional.of(adminCode));

        boolean result = registrationCodeService.validateCode(testCode);

        assertFalse(result);
        verify(registrationCodeRepository, never()).save(any(RegistrationCode.class));
    }
}