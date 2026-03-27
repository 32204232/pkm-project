package com.pkm.store.domain.member.controller;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.SignUpRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.service.AuthService; // [★추가]
import com.pkm.store.domain.member.service.MemberService;
import com.pkm.store.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService; // [★핵심] 인증 전담 서비스 주입

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUpRequest request) {
        Long memberId = memberService.join(request.getEmail(), request.getPassword(), request.getNickname(), Member.Role.USER);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request, 
                                                           HttpServletResponse response) {
        // [수정] memberService가 아니라 authService의 login을 호출합니다!
        TokenResponse tokenResponse = authService.login(request);

        // 쿠키 생성 로직 (보안 설정 유지)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true) // 운영 환경에선 true, 로컬 테스트 시 false 고려
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 프론트엔드엔 Access Token만 반환
        TokenResponse accessOnlyResponse = new TokenResponse(tokenResponse.getAccessToken(), null, tokenResponse.getRole());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공!", accessOnlyResponse));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        
        // [★개선] 컨트롤러에서 직접 검증하지 않고, 서비스에 쿠키값을 넘겨서 새 토큰을 받아옵니다.
        TokenResponse tokenResponse = authService.reissueAccessToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success("토큰 재발급 성공!", tokenResponse));
    }
}