package com.pkm.store.domain.order.service;

import com.pkm.store.domain.cart.repository.CartItemRepository;
import com.pkm.store.domain.order.service.OrderService;
import com.pkm.store.global.exception.CustomException;
import com.pkm.store.global.exception.ErrorCode;
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
    private final OrderService orderService;
    private final CartItemRepository cartItemRepository;

    public Long createOrderSafely(Long memberId) {
        // 1. 장바구니 상품 ID 추출 및 정렬 (데드락 방지)
        List<Long> productIds = getSortedProductIds(memberId);
        
        if (productIds.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        // 2. 멀티락 생성 (여러 상품의 재고를 동시에 선점)
        List<RLock> locks = productIds.stream()
                .map(id -> redissonClient.getLock("product:stock:" + id))
                .collect(Collectors.toList());
        
        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        try {
            // 3. 락 획득 시도 (최대 5초 대기, 10초간 점유)
            boolean isLocked = multiLock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                log.warn("주문 폭주로 인해 락 획득 실패 - memberId: {}", memberId);
                throw new CustomException(ErrorCode.ORDER_TIMEOUT);
            }

            // 4. 트랜잭션이 보장된 주문 로직 실행
            // 주의: 락의 해제가 트랜잭션의 커밋보다 늦어야 정합성이 유지됨
            return orderService.createOrderFromCart(memberId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.SYSTEM_ERROR);
        } finally {
            // 5. 반드시 락 해제
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    private List<Long> getSortedProductIds(Long memberId) {
        return cartItemRepository.findByMemberId(memberId).stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .sorted() // ★중요: 정렬을 해야 데드락(Deadlock)을 방지할 수 있습니다.
                .collect(Collectors.toList());
    }
}