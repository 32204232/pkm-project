package com.pkm.store.domain.product.dto;

import com.pkm.store.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponse {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String imageUrl;

    // Entity를 받아서 DTO로 변환해주는 똑똑한 생성자
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.imageUrl = product.getImageUrl();
    }
}