package com.pkm.store.domain.member.controller;

import com.pkm.store.domain.member.dto.LoginRequest;
import com.pkm.store.domain.member.dto.SignUpRequest;
import com.pkm.store.domain.member.dto.TokenResponse;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.service.MemberService;
import com.pkm.store.global.dto.ApiResponse;

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
public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody SignUpRequest request) {
    Long memberId = memberService.join(request.getEmail(), request.getPassword(), request.getNickname(), Member.Role.USER);
    return ResponseEntity.ok(ApiResponse.success("회원가입 성공!", memberId)); //
}

@PostMapping("/login")
public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
    TokenResponse response = memberService.login(request.getEmail(), request.getPassword());
    return ResponseEntity.ok(ApiResponse.success("로그인 성공!", response)); //
}
}