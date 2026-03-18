package com.pkm.store.domain.product.dto;

import com.pkm.store.domain.product.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private int stockQuantity;

    private String imageUrl; // S3를 쓸 거지만, 텍스트 수정을 위해 껍데기는 남겨둠

    // [★새로 추가된 도메인 정보들★]
    @NotNull(message = "카테고리를 선택해주세요.")
    private Product.Category category;

    @NotNull(message = "시리즈를 선택해주세요.")
    private Product.Series series;

   @NotNull(message = "판매 상태를 선택해주세요.")
    private Product.ProductStatus status;

    @NotNull(message = "발매일을 입력해주세요.")
    private LocalDate releaseDate;
}