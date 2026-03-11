package com.pkm.store.domain.product.service;

import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 데이터를 읽기만 하겠다고 선언 (성능 향상)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * [어드민 전용] 새로운 포켓몬 상자 등록
     */
    @Transactional // 데이터를 변경(저장)하는 곳이므로 별도로 달아줍니다.
    public Long registerProduct(String name, int price, int stockQuantity, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .imageUrl(imageUrl)
                .build();

        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    /**
     * [모두] 전체 상품 목록 조회
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * [모두] 단일 상품 상세 조회
     */
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포켓몬 상자입니다.")); 
                // 나중에 예외 처리를 더 예쁘게 다듬을 겁니다.
    }
}