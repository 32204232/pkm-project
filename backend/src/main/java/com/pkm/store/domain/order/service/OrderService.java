package com.pkm.store.domain.order.service;

import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.repository.CartItemRepository;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.entity.Order;
import com.pkm.store.domain.order.entity.Order.OrderStatus;
import com.pkm.store.domain.order.entity.OrderItem;
import com.pkm.store.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * 장바구니 상품 일괄 주문 (포켓몬 상자 구매)
     */
    @Transactional
    public Long createOrderFromCart(Long memberId) {
        // 1. 주문하는 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 장바구니 상품들 조회
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        // 3. 장바구니 아이템 -> 주문 아이템으로 변환 (이 과정에서 재고가 자동으로 깎임)
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.createOrderItem(cartItem.getProduct(), cartItem.getCount());
            orderItems.add(orderItem);
        }

        // 4. 주문 생성 및 DB 저장 (cascade = CascadeType.ALL 덕분에 OrderItem도 같이 저장됨)
        Order order = Order.createOrder(member, orderItems);
        orderRepository.save(order);

        // 5. 결제 완료된 장바구니 비우기
        cartItemRepository.deleteByMemberId(memberId);

        // 생성된 주문 번호 반환
        return order.getId();
    }

    public Order getOrder(Long orderId, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 남의 주문을 훔쳐보지 못하게 방어
        if (!order.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문 내역만 조회할 수 있습니다.");
        }
        return order;
    }
    @Transactional
    // [★30년차 최적화★] Member Id 대신 Facade에서 락을 걸며 가져온 Member와 CartItem 리스트를 그대로 받습니다.
    public Long createOrderFromCart(Member member, List<CartItem> cartItems) {
        
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            // 재고 감소 로직은 이 안에서 안전하게 실행됨
            OrderItem orderItem = OrderItem.createOrderItem(cartItem.getProduct(), cartItem.getCount());
            orderItems.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItems);
        orderRepository.save(order);

        // 결제 완료된 장바구니 비우기
        cartItemRepository.deleteByMemberId(member.getId());

        return order.getId();
    }
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 이미 완료된 주문은 취소할 수 없도록 방어 (선택 사항)
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 주문은 취소할 수 없습니다.");
        }

        // 1. 주문 상태 변경
        // [주의] Order 엔티티에 setStatus 또는 cancel 메서드를 만들어야 할 수도 있습니다.
        // 현재는 직접 필드 접근이 안 될 수 있으니 Order 엔티티에 아래 메서드를 추가하세요.
        order.cancel(); 

        // 2. 재고 복구 (모든 주문 항목에 대해)
        order.getOrderItems().forEach(orderItem -> {
            orderItem.getProduct().addStock(orderItem.getCount());
        });
    }
    
}