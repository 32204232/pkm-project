package com.pkm.store.domain.product.entity;

import com.pkm.store.global.entity.BaseEntity;
import com.pkm.store.global.exception.CustomException;
import com.pkm.store.global.exception.ErrorCode;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;         // 상품명 (예: "확장팩 스노해저드")

    @Column(nullable = false)
    private int price;           // 가격 (예: 30000)

    @Column(nullable = false)
    private int stockQuantity;   // 재고 (예: 100)

    private String imageUrl;     // S3 이미지 URL

    // [★추가된 실무형 도메인 속성들★]

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;   // 상품 종류 (BOOSTER_BOX, SUPPLY 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Series series;       // 시리즈 (SCARLET_VIOLET 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // 판매 상태 (ON_SALE, OUT_OF_STOCK 등)

    private LocalDate releaseDate; // 발매일 (신상품 오픈런을 위한 날짜!)

    @Builder
    public Product(String name, int price, int stockQuantity, String imageUrl, 
                   Category category, Series series, ProductStatus status, LocalDate releaseDate) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.category = category;
        this.series = series;
        this.status = status;
        this.releaseDate = releaseDate;
    }

    // Enum 정의 (내부 클래스로 두거나 따로 파일을 빼도 무방하다)
    public enum Category { BOOSTER_BOX, STARTER_DECK, SPECIAL_SET, SUPPLY }
    public enum Series { SCARLET_VIOLET, SWORD_SHIELD, SUN_MOON, CLASSIC }
    public enum ProductStatus { PRE_ORDER, ON_SALE, OUT_OF_STOCK, END_OF_SALE }

public void updateProduct(String name, int price, int stockQuantity, String imageUrl, 
                          Category category, Series series, ProductStatus status, java.time.LocalDate releaseDate) {
    this.name = name;
    this.price = price;
    this.stockQuantity = stockQuantity;
    this.imageUrl = imageUrl; //
    this.category = category;
    this.series = series;
    this.status = status;
    this.releaseDate = releaseDate;
}

    // 재고 관리 로직 (나중에 주문 시 사용)
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            // 비즈니스 예외인 CustomException으로 변경하여 프론트에 정확한 에러 전달
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }
        this.stockQuantity = restStock;
    }
    public void addStock(int quantity) {
    this.stockQuantity += quantity;
}
}