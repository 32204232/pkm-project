package com.pkm.store.domain.order.entity;

import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    // [추가] 양방향 연관관계를 위한 Setter (내부용)
    void setOrder(Order order) {
        this.order = order;
    }

    // [추가] 실무형 생성 메서드: 주문 상품이 생성될 때 가격을 고정하고 재고를 바로 깎습니다.
    public static OrderItem createOrderItem(Product product, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.orderPrice = product.getPrice();
        orderItem.count = count;

        product.removeStock(count); // 재고 차감 로직 호출!
        return orderItem;
    }
    // OrderItem.java 안에 추가해주세요!
    public int getTotalPrice() {
        return this.orderPrice * this.count; // (가격 * 수량)
    }
}