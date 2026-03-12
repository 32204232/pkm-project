package com.pkm.store.domain.member.controller;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.SignUpRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 프론트와 JSON으로 통신하겠다고 선언
@RequestMapping("/api/members") // 이 컨트롤러의 기본 주소
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
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
        // [★수정됨★] 서비스(Service)가 아예 TokenResponse(토큰+권한)를 통째로 만들어 오게 시킨다!
        TokenResponse response = memberService.login(request.getEmail(), request.getPassword());
        
        // 프론트엔드에게 정상(200 OK) 상태로 포장해서 던져줌
        return ResponseEntity.ok(response);
    }
}