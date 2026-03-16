package com.pkm.store.domain.order.service;

import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConcurrencyFacade {

    private final RedissonClient redissonClient;
    private final OrderService orderService; // 기존에 만드신 트랜잭션 서비스
    private final CartItemRepository cartItemRepository;

    /**
     * 오픈런 대응: 장바구니 일괄 주문 시 완벽한 동시성 제어
     */
    public Long createOrderSafely(Long memberId) {
        // 1. 장바구니 상품 미리 조회
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        // 2. ★매우 중요★ 데드락(Deadlock) 방지를 위해 상품 ID를 오름차순으로 정렬합니다.
        // 정렬하지 않으면 A고객과 B고객이 서로 다른 순서로 락을 잡으려다 서버가 영원히 멈춥니다.
        List<Long> productIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .sorted() 
                .collect(Collectors.toList());

        // 3. 정렬된 상품 ID를 기반으로 Redis 락 객체 리스트 생성
        List<RLock> locks = productIds.stream()
                .map(id -> redissonClient.getLock("product:stock:" + id))
                .collect(Collectors.toList());

        // 4. 여러 상품의 락을 한 번에 제어하는 MultiLock 생성
        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        try {
            // 5. 락 획득 시도: 최대 5초간 대기, 락을 잡은 후 3초 뒤 자동 해제(장애 방지)
            log.info("회원 {}의 다중 상품 락 획득 시도 중...", memberId);
            boolean isLocked = multiLock.tryLock(5, 3, TimeUnit.SECONDS);
            
            if (!isLocked) {
                // 오픈런 시 수만 명이 몰려 5초 안에 락을 못 잡으면 대기열 지연 메시지를 띄웁니다.
                throw new RuntimeException("현재 주문량이 폭주하여 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.");
            }

            // 6. 락 획득 성공! 안전하게 DB 트랜잭션(기존 로직)을 실행합니다.
            return orderService.createOrderFromCart(memberId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("주문 처리 중 시스템 인터럽트가 발생했습니다.");
        } finally {
            // 7. 로직이 끝나면 반드시 락을 해제합니다.
            try {
                multiLock.unlock();
                log.info("회원 {}의 락 안전하게 해제 완료", memberId);
            } catch (IllegalMonitorStateException e) {
                log.warn("이미 해제되었거나 소유하지 않은 락입니다.");
            }
        }
    }
}