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

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConcurrencyFacade {

    private final RedissonClient redissonClient;
    private final OrderService orderService;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;

    public Long createOrderSafely(Long memberId) {
        // [★30년차 최적화★] 파사드에서 1번만 조회하고 Service로 넘겨 DB I/O를 반으로 줄임
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
                
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        if (cartItems.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        // 1. 장바구니 상품 ID 추출 및 정렬 (데드락 방지용 정렬 유지)
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

        try {
            // 3. 락 획득 시도
            boolean isLocked = multiLock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                log.warn("주문 폭주로 인해 락 획득 실패 - memberId: {}", memberId);
                // "주문이 폭주하고 있습니다. 잠시 후 다시 시도해주세요." 커스텀 에러 던지기
                throw new CustomException(ErrorCode.ORDER_TIMEOUT); 
            }

            // 4. 트랜잭션이 걸린 Service 호출 (미리 조회한 객체들을 넘겨줌)
            // @Transactional이 걸린 내부 메서드가 완료(커밋)된 후 락이 해제됨 -> 완벽한 정합성 보장!
            return orderService.createOrderFromCart(member, cartItems);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.SYSTEM_ERROR);
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }
}