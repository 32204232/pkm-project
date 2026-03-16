package com.pkm.store.domain.payment.controller;


import com.pkm.store.domain.payment.service.PaymentService;
import com.pkm.store.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmPayment(@RequestBody Map<String, Object> requestBody) {
        
        // 프론트엔드가 토스 SDK에서 받아온 3가지 핵심 정보를 꺼냅니다.
        String paymentKey = (String) requestBody.get("paymentKey");
        String orderId = (String) requestBody.get("orderId"); // 우리가 만든 orderUid
        int amount = (Integer) requestBody.get("amount");

        // 금고지기(Service)에게 위변조 검증 및 최종 승인 지시!
        paymentService.confirmPayment(paymentKey, orderId, amount);

        return ResponseEntity.ok(ApiResponse.success("결제가 성공적으로 완료되었습니다 삐까!", null));
    }
}