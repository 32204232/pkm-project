package com.pkm.store.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request (클라이언트의 잘못된 요청)
    INVALID_INPUT_VALUE(400, "잘못된 입력값입니다."),
    OUT_OF_STOCK(400, "포켓몬 박스의 재고가 부족합니다."),
    CART_EMPTY(400, "장바구니가 비어 있어 주문할 수 없습니다."), // [추가]

    // 401 Unauthorized (인증 실패)
    UNAUTHORIZED(401, "인증이 필요합니다. 다시 로그인해주세요."),
    INVALID_TOKEN(401, "유효하지 않거나 만료된 토큰입니다."),

    // 403 Forbidden (권한 없음)
    ACCESS_DENIED(403, "관리자만 접근할 수 있는 기능입니다."),

    // 404 Not Found (데이터 없음)
    MEMBER_NOT_FOUND(404, "존재하지 않는 트레이너(회원)입니다."),
    PRODUCT_NOT_FOUND(404, "존재하지 않는 상품입니다."),
    CART_NOT_FOUND(404, "장바구니 정보를 찾을 수 없습니다."),

    // 429 Too Many Requests (오픈런 등 동시성 제어 시 사용)
    ORDER_TIMEOUT(429, "현재 주문량이 많아 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요."), // [추가]

    // 500 Internal Server Error (서버 내부 문제)
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다. 개발팀에 문의해주세요."),
    SYSTEM_ERROR(500, "시스템 장애가 발생했습니다."); // [추가]

    private final int status;
    private final String message;
}