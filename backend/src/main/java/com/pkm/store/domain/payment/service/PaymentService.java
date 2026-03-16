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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    
    // HTTP 통신을 위한 스프링 기본 템플릿
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    /**
     * [최종 결제 승인] 프론트엔드에서 인증받은 paymentKey를 토스 서버에 던져서 최종 승인을 받습니다.
     */
    @Transactional
    public void confirmPayment(String paymentKey, String orderUid, int amount) {
        // 1. 우리 DB에서 주문 내역 조회
        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. ★보안 핵심★ 프론트엔드에서 보낸 금액(조작 가능성 있음)과 DB의 실제 상품 가격 비교
        // (order 엔티티에 getTotalPrice() 메서드가 있다고 가정)
        if (order.getTotalPrice() != amount) {
            throw new IllegalArgumentException("결제 금액 위변조가 의심됩니다. 결제를 중단합니다.");
        }

        // 3. 토스페이먼츠 API로 전송할 헤더(Header) 생성 (Basic Auth 방식)
        HttpHeaders headers = new HttpHeaders();
        // 토스페이먼츠 시크릿 키 뒤에 콜론(:)을 붙여 Base64로 인코딩해야 합니다. (토스 공식 문서 규칙)
        String encodedAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. 토스페이먼츠 API로 전송할 바디(Body) 생성
        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderUid);
        params.put("amount", amount);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        try {
            // 5. 토스 서버로 '결제 최종 승인' POST 요청 발사!
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    requestEntity,
                    String.class
            );

            // 6. 200 OK가 떨어지면 진짜 고객 돈이 빠져나간 것입니다.
            if (response.getStatusCode().is2xxSuccessful()) {
                order.completePayment(paymentKey); // 주문 상태 COMPLETED로 변경!
                log.info("결제 최종 승인 완료! Order UID: {}", orderUid);
            }

        } catch (Exception e) {
            // 토스 서버 통신 실패 (잔액 부족, 카드 정지 등)
            log.error("토스페이먼츠 승인 실패: {}", e.getMessage());
            
            // ★CTO의 팁: 여기서 재고를 원상복구(Rollback)하는 로직을 호출해야 합니다!
            // order.cancel(); 
            
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다.");
        }
    }
}