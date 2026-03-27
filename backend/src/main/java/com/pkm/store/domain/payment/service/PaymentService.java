package com.pkm.store.domain.payment.service;

import com.pkm.store.domain.order.entity.Order;
import com.pkm.store.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

// backend/src/main/java/com/pkm/store/domain/payment/service/PaymentService.java

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void confirmPayment(String paymentKey, String orderUid, int amount) {
        // 1. 주문 조회 및 상태 확인 (멱등성 처리)
        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            log.info("이미 처리 완료된 주문입니다. UID: {}", orderUid);
            return; 
        }

        // 2. 금액 검증
        if (order.getTotalPrice() != amount) {
            log.error("금액 위변조 감지! DB: {}, 요청: {}", order.getTotalPrice(), amount);
            // 위변조 시에도 주문을 취소하고 재고를 돌려놓는 것이 안전함
            order.cancel(); 
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        // 3. 토스 인증 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(tossSecretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderUid);
        params.put("amount", amount);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                order.completePayment(paymentKey);
                log.info("결제 승인 성공! Order UID: {}", orderUid);
            } else {
                // [★추가] 승인 거절 (잔액 부족 등) 시 재고 복구
                log.error("결제 승인 실패: {}", response.getBody());
                order.cancel();
                throw new RuntimeException("결제 승인이 거절되었습니다.");
            }
        } catch (Exception e) {
            // [★추가] 네트워크 장애나 타임아웃 발생 시 안전하게 재고 복구
            log.error("토스 서버 통신 중 에러 발생: {}", e.getMessage());
            order.cancel();
            throw new RuntimeException("결제 처리 중 장애가 발생하여 주문이 자동 취소되었습니다.");
        }
    }

    /**
     * [웹훅 처리 로직]
     */
    @Transactional
    public void processWebhook(Map<String, Object> payload) {
        String orderId = (String) payload.get("orderId");
        String status = (String) payload.get("status");
        String paymentKey = (String) payload.get("paymentKey");

        Order order = orderRepository.findByOrderUid(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if ("DONE".equals(status)) {
            if (order.getStatus() != Order.OrderStatus.COMPLETED) {
                order.completePayment(paymentKey);
                log.info("웹훅: 결제 완료 처리 성공 - Order UID: {}", orderId);
            }
        } else if ("CANCELED".equals(status)) {
            // [★수정] 웹훅으로 취소 신호가 와도 엔티티의 cancel()을 호출하여 재고 복구
            if (order.getStatus() != Order.OrderStatus.CANCELED) {
                order.cancel();
                log.info("웹훅: 결제 취소 및 재고 복구 완료 - Order UID: {}", orderId);
            }
        }
    }
}