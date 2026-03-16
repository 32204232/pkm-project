package com.pkm.store.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken; // [★추가★] 서버와 컨트롤러 사이에서 전달할 리프레시 토큰
    private String role;         // 프론트엔드에서 관리자/일반유저 화면을 구분하기 위한 역할
}