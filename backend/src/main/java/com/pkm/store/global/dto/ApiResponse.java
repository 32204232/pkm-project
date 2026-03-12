package com.pkm.store.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data; // 실제 반환할 데이터가 들어가는 곳

    // 성공 - 데이터만 반환할 때
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "요청에 성공했습니다.", data);
    }

    // 성공 - 커스텀 메시지와 데이터를 반환할 때
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data);
    }

    // 에러 발생 시 (데이터는 null 처리)
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, status, message, null);
    }
}