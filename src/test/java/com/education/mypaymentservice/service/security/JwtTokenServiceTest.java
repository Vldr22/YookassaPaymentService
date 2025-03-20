package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.model.enums.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private String secretKeyValue;
    private String testPhone;
    private String normalizedPhone;
    private String testEmail;

    @BeforeEach
    void setUp() {
        secretKeyValue = "bGV0bWVzaG93eW91YXZhbGlkYmFzZTY0c3RyaW5ndGhhdGlzbG9uZ2Vub3VnaGZvcmhzNTEyc2lnbmluZ2p3dHRva2Vuc2FuZGl0c2hvdWxkcGFzcw==";
        testPhone = "+7(900)123-45-67";
        normalizedPhone = "79001234567";
        testEmail = "test@example.com";

        jwtTokenService = new JwtTokenService(secretKeyValue);

        ReflectionTestUtils.setField(jwtTokenService, "tokenExpirationForClient", 3600L);
        ReflectionTestUtils.setField(jwtTokenService, "tokenExpirationForEmployee", 7200L);
        ReflectionTestUtils.setField(jwtTokenService, "tokenExpirationForAdmin", 10800L);
    }

    @Test
    public void constructor_WithValidSecretKey_ShouldCreateInstance() {
        assertNotNull(jwtTokenService);
    }

    @Test
    public void constructor_WithInvalidKey_ShouldThrowWeakKeyException() {
        String invalidKey = "invalidKey";

        Exception exception = assertThrows(SecurityException.class, () -> {
            new JwtTokenService(invalidKey);
        });

        assertTrue(exception.getMessage().contains("Секретный ключ JWT должен быть допустимой строкой в кодировке Base64"));
        assertInstanceOf(WeakKeyException.class, exception.getCause());
    }

    @Test
    public void generateToken_ForClient_ShouldCreateValidToken() {
        String token = jwtTokenService.generateToken(testPhone, Roles.ROLE_CLIENT);

        assertNotNull(token);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(normalizedPhone, claims.getSubject());
        assertEquals("ROLE_CLIENT", claims.get("role"));

        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiration = claims.getExpiration().toInstant();

        long durationSeconds = expiration.getEpochSecond() - issuedAt.getEpochSecond();
        assertEquals(3600L, durationSeconds);
    }

    @Test
    public void generateToken_ForEmployee_ShouldCreateValidToken() {
        String token = jwtTokenService.generateToken(testEmail, Roles.ROLE_EMPLOYEE);

        assertNotNull(token);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(testEmail, claims.getSubject());
        assertEquals("ROLE_EMPLOYEE", claims.get("role"));

        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiration = claims.getExpiration().toInstant();

        long durationSeconds = expiration.getEpochSecond() - issuedAt.getEpochSecond();
        assertEquals(7200L, durationSeconds);
    }

    @Test
    public void generateToken_ForAdmin_ShouldCreateValidToken() {
        String token = jwtTokenService.generateToken(testEmail, Roles.ROLE_ADMIN);

        assertNotNull(token);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(testEmail, claims.getSubject());
        assertEquals("ROLE_ADMIN", claims.get("role"));

        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiration = claims.getExpiration().toInstant();

        long durationSeconds = expiration.getEpochSecond() - issuedAt.getEpochSecond();
        assertEquals(10800L, durationSeconds);
    }

    @Test
    public void extractSubjectByJWTToken_WithValidToken_ShouldReturnSubject() {
        String token = jwtTokenService.generateToken(testEmail, Roles.ROLE_EMPLOYEE);

        String extractedSubject = jwtTokenService.extractSubjectByJWTToken(token);

        assertEquals(testEmail, extractedSubject);
    }

    @Test
    public void extractRoleByJWTToken_WithValidToken_ShouldReturnRole() {
        String token = jwtTokenService.generateToken(testEmail, Roles.ROLE_ADMIN);

        String extractedRole = jwtTokenService.extractRoleByJWTToken(token);

        assertEquals("ROLE_ADMIN", extractedRole);
    }

    @Test
    public void extractSubjectByJWTToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalidToken";

        assertThrows(Exception.class, () -> {
            jwtTokenService.extractSubjectByJWTToken(invalidToken);
        });
    }

    @Test
    public void extractRoleByJWTToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalidToken";

        assertThrows(Exception.class, () -> {
            jwtTokenService.extractRoleByJWTToken(invalidToken);
        });
    }
}
