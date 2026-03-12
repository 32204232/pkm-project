package com.pkm.store.domain.product.dto;

import com.pkm.store.domain.product.entity.Product;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String imageUrl;

    // [★새로 추가된 도메인 정보들★]
    private String category;
    private String series;
    private String status;
    private LocalDate releaseDate;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.imageUrl = product.getImageUrl();
        
        // Enum은 프론트에서 쓰기 편하게 문자열(String)로 변환해서 던져준다!
        this.category = product.getCategory().name();
        this.series = product.getSeries().name();
        this.status = product.getStatus().name();
        this.releaseDate = product.getReleaseDate();
    }
}