package com.pkm.store.domain.order.entity;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // [★추가] 고유 결제 ID 생성을 위해 필요

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // [★핵심] 토스 결제창에 보낼 절대 중복되지 않는 난수 (초당 1만 건이 들어와도 안전함)
    @Column(unique = true, nullable = false)
    private String orderUid; 

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum OrderStatus { PENDING, COMPLETED, CANCELED }

    // [결제 완료 처리]
    public void completePayment(String paymentKey) {
        this.status = OrderStatus.COMPLETED;
        this.paymentKey = paymentKey;
    }

    // 양방향 연관관계 편의 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // [★추가] PaymentService에서 결제 금액 위변조를 검증하기 위해 꼭 필요한 총액 계산 로직!
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); 
        }
        return totalPrice;
    }

    // [★수정] 실무형 생성 메서드: 주문 생성 시 PENDING 상태 부여 및 UUID 발급
    public static Order createOrder(Member member, List<OrderItem> orderItems) {
        Order order = new Order();
        order.member = member;
        order.status = OrderStatus.PENDING; // [★수정] 결제 대기 상태로 시작!
        order.orderUid = UUID.randomUUID().toString(); // [★수정] 주문 시점에 난수 즉시 발급!
        
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }
    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 주문은 취소할 수 없습니다.");
        }
        
        // 중복 취소 방지 (이미 취소된 경우 무시)
        if (this.status == OrderStatus.CANCELED) {
            return;
        }

        this.status = OrderStatus.CANCELED;
        
        // 주문 항목을 돌며 각 상품의 재고를 원복함
        for (OrderItem orderItem : orderItems) {
            orderItem.getProduct().addStock(orderItem.getCount());
        }
    }
    
}