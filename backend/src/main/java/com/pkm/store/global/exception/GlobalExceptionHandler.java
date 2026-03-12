package com.pkm.store.global.exception;

import com.pkm.store.global.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice // 모든 컨트롤러에서 발생하는 예외를 감지
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 실행 중 발생하는 커스텀 예외 처리
     * ex) throw new CustomException(ErrorCode.OUT_OF_STOCK);
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("비즈니스 예외 발생 : {}", e.getErrorCode().getMessage());
        
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode().getStatus(), e.getErrorCode().getMessage()));
    }

    /**
     * 우리가 미처 예측하지 못한 모든 예외 처리 (500 에러 방어)
     * 널포인터(NPE)나 DB 접속 불량 등이 터져도 하얀 에러 화면이 나가지 않도록 막아줍니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상치 못한 서버 에러 발생 : ", e); // 에러의 상세 내용(Stack Trace)을 로그로 남김
        
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(500, ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage())
    );

    log.warn("Validation 실패: {}", errors);

    return ResponseEntity
            .status(400)
            .body(new ApiResponse<>(false, 400, "입력값이 올바르지 않습니다.", errors));
}
}