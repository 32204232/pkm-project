package com.pkm.store.domain.order.service;

import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.repository.CartItemRepository;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
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

// backend/src/main/java/com/pkm/store/domain/order/service/OrderConcurrencyFacade.java

// backend/src/main/java/com/pkm/store/domain/order/service/OrderConcurrencyFacade.java

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConcurrencyFacade {

    private final RedissonClient redissonClient;
    private final OrderService orderService;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;

    public Long createOrderSafely(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
                
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        if (cartItems.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        // 1. 상품 ID 추출 및 정렬 (데드락 방지)
        List<Long> productIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 2. 멀티락 생성
        List<RLock> locks = productIds.stream()
                .map(id -> redissonClient.getLock("product:stock:" + id))
                .collect(Collectors.toList());
        
        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));

        // [★수정] isLocked 변수를 try 밖에서 선언하여 finally에서 사용 가능하게 함
        boolean isLocked = false; 

        try {
            // 3. 락 획득 시도 (최대 5초 대기, 10초간 점유)
            isLocked = multiLock.tryLock(5, 10, TimeUnit.SECONDS);
            
            if (!isLocked) {
                log.warn("주문 폭주로 인해 락 획득 실패 - memberId: {}", memberId);
                throw new CustomException(ErrorCode.ORDER_TIMEOUT); 
            }

            // 4. 비즈니스 로직 실행 (미리 조회한 객체 전달로 DB I/O 최적화)
            return orderService.createOrderFromCart(member, cartItems);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.SYSTEM_ERROR);
        } finally {
            // [★핵심 수정] MultiLock은 boolean 플래그로 해제 여부를 결정하는 것이 가장 안전함
            if (isLocked) {
                try {
                    multiLock.unlock();
                } catch (IllegalMonitorStateException e) {
                    log.error("이미 해제된 락을 해제하려 했습니다: {}", e.getMessage());
                }
            }
        }
    }
}