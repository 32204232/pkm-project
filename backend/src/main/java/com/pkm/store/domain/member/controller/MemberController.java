package com.pkm.store.domain.member.controller;

import com.pkm.store.domain.member.dto.SignUpRequest;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.TokenResponse;

@RestController // 프론트와 JSON으로 통신하겠다고 선언
@RequestMapping("/api/members") // 이 컨트롤러의 기본 주소
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        // 프론트에서 보낸 JSON을 SignUpRequest 객체로 받아서 Service로 넘깁니다.
        // 현재는 모두 USER 권한으로 가입시킵니다.
        Long memberId = memberService.join(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                Member.Role.USER
        );
        return ResponseEntity.ok("회원가입 성공! 회원 번호: " + memberId);
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        // 서비스에서 토큰을 받아옵니다.
        String token = memberService.login(request.getEmail(), request.getPassword());
        
        // 프론트엔드에게 토큰을 담아서 응답합니다.
        return ResponseEntity.ok(new TokenResponse(token));
    }
    
}