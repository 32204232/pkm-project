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
    private final long expiration;

    // application.yml에 적어둔 비밀키와 만료시간을 가져옵니다.
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // 1. 토큰 생성 (로그인 성공 시 프론트엔드에게 줄 출입증)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email) // 토큰의 주인 (이메일)
                .claim("role", role) // 권한 정보 (USER, ADMIN)
                .issuedAt(new Date()) // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(secretKey) // 우리 서버만의 비밀 도장 쾅!
                .compact();
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