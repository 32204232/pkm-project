package com.pkm.store.domain.order.dto;

import com.pkm.store.domain.order.entity.Order;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// backend/src/main/java/com/pkm/store/domain/order/dto/OrderResponse.java 수정

@Getter
public class OrderResponse {
    private Long orderId;
    private String orderUid;   // [★추가] 토스 결제용 UUID
    private String orderName;  // [★추가] "피카츄 외 1건" 식의 상품명
    private LocalDateTime orderDate;
    private int totalPrice;
    private List<OrderItemResponse> orderItems;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderUid = order.getOrderUid(); //
        this.orderDate = order.getCreatedAt();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
        this.totalPrice = order.getTotalPrice(); //

        // [★실무 팁] 주문 명칭 생성 로직
        if (!this.orderItems.isEmpty()) {
            String firstName = this.orderItems.get(0).getProductName();
            this.orderName = this.orderItems.size() > 1 
                ? firstName + " 외 " + (this.orderItems.size() - 1) + "건" 
                : firstName;
        }
    }
}