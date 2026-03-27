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
        String paymentKey = (String) requestBody.get("paymentKey");
        String orderId = (String) requestBody.get("orderId");
        int amount = (Integer) requestBody.get("amount");

        paymentService.confirmPayment(paymentKey, orderId, amount);

        return ResponseEntity.ok(ApiResponse.success("결제 성공!", null));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        paymentService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}