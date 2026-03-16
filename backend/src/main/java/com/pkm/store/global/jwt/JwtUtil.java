package com.pkm.store.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-expiration:900000}") long accessExpiration,    // 기본 15분
                   @Value("${jwt.refresh-expiration:604800000}") long refreshExpiration) { // 기본 7일
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // 1. Access Token 생성 (짧은 수명)
    public String generateAccessToken(String email, String role) {
        return createToken(email, role, accessExpiration);
    }

    // 2. Refresh Token 생성 (긴 수명)
    public String generateRefreshToken(String email) {
        // Refresh Token은 권한 정보 없이 이메일(식별자)만 가집니다.
        return createToken(email, null, refreshExpiration);
    }

    private String createToken(String email, String role, long expirationTime) {
        var builder = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey);
                
        if (role != null) {
            builder.claim("role", role);
        }
        return builder.compact();
    }
    // 2. 프론트엔드가 가져온 토큰에서 이메일 빼내기
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 3. 토큰에서 권한(Role) 빼내기
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 4. 토큰이 유효한지(위조되지 않았는지, 만료되지 않았는지) 확인
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}