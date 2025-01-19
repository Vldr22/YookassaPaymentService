package com.education.mypaymentservice.service.security;

import com.education.mypaymentservice.model.enums.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.education.mypaymentservice.utils.NormalizeUtils.normalizeRussianPhoneNumber;

@Service
public class JwtTokenService {

    private final Key secretKey;

    @Value("${jwt.token.expiration.client.second}")
    private long tokenExpirationForClient;

    @Value("${jwt.token.expiration.employee.second}")
    private long tokenExpirationForEmployee;

    @Value("${jwt.token.expiration.admin.second}")
    private long tokenExpirationForAdmin;

    public JwtTokenService(@Value("${jwt.token.secret.key}") String secretKeyValue) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKeyValue);
            if (keyBytes.length < 64) {
                throw new WeakKeyException("Предоставленный ключ слишком короток для HS512");
            }
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new SecurityException(
                    "Секретный ключ JWT должен быть допустимой строкой в кодировке Base64 длиной >= 64 байт", e);
        }
    }

    public String generateToken(String subject, Roles role) {
        Instant now = Instant.now();
        Instant expiration = null;
        String sub = subject;

        switch (role) {
            case ROLE_CLIENT -> {
                expiration = now.plus(tokenExpirationForClient, ChronoUnit.SECONDS);
                sub = normalizeRussianPhoneNumber(subject);
            }
            case ROLE_EMPLOYEE -> expiration = now.plus(tokenExpirationForEmployee, ChronoUnit.SECONDS);
            case ROLE_ADMIN -> expiration = now.plus(tokenExpirationForAdmin, ChronoUnit.SECONDS);
        }

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claim("role", role)
                .subject(sub)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractSubjectByJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extractRoleByJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}
