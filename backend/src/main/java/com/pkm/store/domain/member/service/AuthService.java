package com.pkm.store.domain.member.service;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

        redisTemplate.opsForValue().set(
                "RT:" + member.getEmail(),
                refreshToken,
                7, TimeUnit.DAYS
        );

        return new TokenResponse(accessToken, refreshToken, member.getRole().name());
    }

    @Transactional(readOnly = true)
    public TokenResponse reissueAccessToken(String refreshToken) {
        // 1. 토큰 자체가 유효한지 검사
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. Redis에 저장된 토큰과 일치하는지 확인 (보안 핵심!)
        String email = jwtUtil.getEmailFromToken(refreshToken);
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);
        
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("변조되었거나 만료된 세션입니다.");
        }

        // 3. 사용자 정보 재조회 및 새 토큰 생성
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.generateAccessToken(email, member.getRole().name());
        
        return new TokenResponse(newAccessToken, null, member.getRole().name());
    }
}