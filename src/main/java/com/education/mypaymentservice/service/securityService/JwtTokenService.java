package com.education.mypaymentservice.service.securityService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenService {

    private final Key secretKey;

    @Value("${JWT_TOKEN_EXPIRATION}")
    private long tokenExpiration;

    //Понятие не имею как работает кодировка и зачем она не тут нужна... Однако без нее у меня не работал
    //введенный ключ шифрования в .env. Хотя пробовал использовать значения сгенерированные самим Spring Security
    public JwtTokenService(@Value("${JWT_TOKEN_SECRET_KEY}") String base64SecretKey) {
        byte[] decodedKey;
        try {
            decodedKey = Base64.getDecoder().decode(base64SecretKey);
        } catch (IllegalArgumentException e) {
            decodedKey = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        }
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateJWTToken(String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpiration * 1000);

        return Jwts.builder()
                .setSubject(phone)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractPhoneByJWTToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
