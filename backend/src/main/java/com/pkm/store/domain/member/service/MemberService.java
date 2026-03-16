package com.pkm.store.domain.member.service;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.global.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.crypto.password.PasswordEncoder; // [추가됨]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // [추가됨] 암호화 도구 주입!
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    // 회원가입 로직
    @Transactional
    public Long join(String email, String password, String nickname, Member.Role role) {
        
        // 1. 중복 이메일 검증
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (핵심 로직 추가!)
        // 사용자가 '1234'를 입력해도 DB에는 '$2a$10$...' 같이 복잡한 문자로 저장됩니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 멤버 객체 생성 (암호화된 비밀번호를 넣습니다)
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword) 
                .nickname(nickname)
                .role(role)
                .build();

        // 4. DB에 저장
        Member savedMember = memberRepository.save(member);
        
        return savedMember.getId();
    }
    @Transactional
    public TokenResponse login(LoginRequest request) {  
    Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    // 1. 두 개의 토큰 발급
    String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getRole().name());
    String refreshToken = jwtUtil.generateRefreshToken(member.getEmail());

    // 2. Redis에 Refresh Token 저장 (Key: email, Value: refreshToken, TTL: 7일)
    // (RedisTemplate<String, String> redisTemplate 주입 필요)
    redisTemplate.opsForValue().set(
            "RT:" + member.getEmail(), 
            refreshToken, 
            7, TimeUnit.DAYS
    );

    // DTO에는 Access Token만 담아서 반환 (Refresh Token은 컨트롤러에서 쿠키로 구울 예정)
return new TokenResponse(accessToken, refreshToken, member.getRole().name());}
    
}