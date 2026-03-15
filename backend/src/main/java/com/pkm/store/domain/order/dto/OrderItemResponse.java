package com.pkm.store.domain.order.dto;

import com.pkm.store.domain.order.entity.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponse {
    private String productName;
    private int orderPrice;
    private int count;
    private String imageUrl;

    public OrderItemResponse(OrderItem orderItem) {
        this.productName = orderItem.getProduct().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
        this.imageUrl = orderItem.getProduct().getImageUrl();
    }
}