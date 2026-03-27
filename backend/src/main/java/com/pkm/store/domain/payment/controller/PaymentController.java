package com.pkm.store.domain.payment.controller;

import com.pkm.store.domain.order.dto.OrderResponse; // [★추가]
import com.pkm.store.domain.order.entity.Order; // [★추가]
import com.pkm.store.domain.order.repository.OrderRepository; // [★추가]
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
    private final OrderRepository orderRepository; // [★핵심 수정] 레포지토리 주입 추가

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmPayment(@RequestBody Map<String, Object> requestBody) {
        String paymentKey = (String) requestBody.get("paymentKey");
        String orderId = (String) requestBody.get("orderId"); // UUID(orderUid) 문자열
        
        // 토스에서 오는 amount는 Integer일 확률이 높으므로 안전하게 변환
        int amount = Integer.parseInt(requestBody.get("amount").toString());

        // 1. 결제 서비스 호출 (승인 처리)
        paymentService.confirmPayment(paymentKey, orderId, amount);

        // 2. [★핵심 수정] 승인된 주문 정보를 찾아 OrderResponse로 변환
        Order order = orderRepository.findByOrderUid(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 3. 영수증 출력을 위해 성공 메시지와 함께 주문 상세 정보를 반환합니다.
        return ResponseEntity.ok(ApiResponse.success("결제 성공!", new OrderResponse(order)));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        paymentService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}