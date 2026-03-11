package com.pkm.store.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken; // 프론트엔드가 저장할 JWT 토큰
}