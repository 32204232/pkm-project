package com.pkm.store.domain.member.controller;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.SignUpRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository; // [★추가]
import com.pkm.store.domain.member.service.MemberService;
import com.pkm.store.global.dto.ApiResponse;
import com.pkm.store.global.jwt.JwtUtil; // [★추가]

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate; // [★추가]
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    // [★핵심 수정] reissue 메서드에서 사용하기 위해 의존성 3가지를 추가 주입받습니다.
    private final JwtUtil jwtUtil; 
    private final RedisTemplate<String, String> redisTemplate; 
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUpRequest request) {
        Long memberId = memberService.join(request.getEmail(), request.getPassword(), request.getNickname(), Member.Role.USER);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request, 
                                                            HttpServletResponse response) {
        // 서비스에서 토큰 2개 생성 및 Redis 저장
        TokenResponse tokenResponse = memberService.login(request);

        // ★ 핵심: Refresh Token을 XSS 공격으로부터 안전한 HttpOnly 쿠키로 생성 ★
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .maxAge(7 * 24 * 60 * 60) // 7일
                .path("/")
                .secure(true)     // HTTPS 환경에서만 전송 (로컬 HTTP 환경에서 테스트할 때는 잠시 false로 변경하세요!)
                .httpOnly(true)   // 자바스크립트(document.cookie)로 접근 절대 불가!
                .sameSite("Strict") // CSRF 공격 방어
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 프론트엔드(React)에게는 15분짜리 Access Token만 JSON으로 넘겨줍니다.
             TokenResponse accessOnlyResponse = new TokenResponse(tokenResponse.getAccessToken(), null, tokenResponse.getRole());
    return ResponseEntity.ok(ApiResponse.success("로그인 성공 삐까!", accessOnlyResponse));                                           
        
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);

        // Redis에 저장된 진짜 Refresh Token과 비교 (탈취 방지)
        String redisRefreshToken = redisTemplate.opsForValue().get("RT:" + email);
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new IllegalArgumentException("변조된 토큰입니다. 다시 로그인해주세요.");
        }

        // 유저 권한 다시 조회 (orElseThrow에 안전장치 추가)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 새로운 Access Token 발급
        String newAccessToken = jwtUtil.generateAccessToken(email, member.getRole().name());
        return ResponseEntity.ok(ApiResponse.success("토큰 재발급 성공!", new TokenResponse(newAccessToken, null, member.getRole().name())));    }
}