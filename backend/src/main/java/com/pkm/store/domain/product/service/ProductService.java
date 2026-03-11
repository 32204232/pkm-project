package com.pkm.store.domain.product.service;

import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * [어드민 전용] 상품 등록
     */
    @Transactional
    public Long registerProduct(String name, int price, int stockQuantity, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .imageUrl(imageUrl)
                .build();

        return productRepository.save(product).getId();
    }

    /**
     * [어드민 전용] 상품 수정 (더티 체킹 활용)
     */
    @Transactional
    public void updateProduct(Long id, String name, int price, int stockQuantity, String imageUrl) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        
        // 엔티티의 메서드를 호출하면 영속성 컨텍스트가 변경을 감지해 DB에 반영합니다.
        product.updateProduct(name, price, stockQuantity, imageUrl);
    }

    /**
     * [어드민 전용] 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        productRepository.delete(product);
    }

    /**
     * 전체 상품 조회
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 단일 상품 상세 조회
     */
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포켓몬 상자입니다."));
    }
}