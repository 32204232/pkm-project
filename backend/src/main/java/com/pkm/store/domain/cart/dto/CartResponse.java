package com.pkm.store.domain.cart.dto;

import com.pkm.store.domain.cart.entity.CartItem;
import lombok.Getter;

@Getter
public class CartResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int price;
    private int count;
    private String imageUrl;

    // Entity -> DTO 변환
    public CartResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.price = cartItem.getProduct().getPrice();
        this.count = cartItem.getCount();
        this.imageUrl = cartItem.getProduct().getImageUrl();
    }
}