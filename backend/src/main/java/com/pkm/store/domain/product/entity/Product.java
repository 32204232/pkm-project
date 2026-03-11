package com.pkm.store.domain.product.entity;
import com.pkm.store.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 예: 1세대 포켓몬 상자

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockQuantity; // 재고

    private String imageUrl; // S3 등에 올라간 이미지 주소

    @Builder
    public Product(String name, int price, int stockQuantity, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    // 비즈니스 로직: 재고 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new RuntimeException("재고가 부족합니다."); // 나중에 커스텀 예외로 변경
        }
        this.stockQuantity = restStock;
    }
}