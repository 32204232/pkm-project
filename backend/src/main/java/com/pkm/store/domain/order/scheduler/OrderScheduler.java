package com.pkm.store.domain.order.scheduler;

import com.pkm.store.domain.order.entity.Order.OrderStatus; // [★수정] 올바른 경로
import com.pkm.store.domain.order.entity.Order;
import com.pkm.store.domain.order.repository.OrderRepository;
import com.pkm.store.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * 10분마다 실행하며, 30분 동안 결제가 안 된 PENDING 상태의 주문을 취소합니다.
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cancelUnpaidOrders() {
        // 현재 시간 기준 30분 전
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        
        // [★수정] ORDER_CREATED 대신 엔티티에 정의된 PENDING 사용
        List<Order> timeoutOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.PENDING, threshold);

        for (Order order : timeoutOrders) {
            try {
                log.info("결제 시간 초과로 인한 주문 자동 취소 처리 시작: 주문ID={}", order.getId());
                orderService.cancelOrder(order.getId());
            } catch (Exception e) {
                log.error("주문 자동 취소 중 오류 발생 (주문ID: {}): {}", order.getId(), e.getMessage());
            }
        }
    }
}