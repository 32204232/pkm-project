package com.pkm.store.domain.product.controller;

import com.pkm.store.domain.product.dto.ProductCreateRequest;
import com.pkm.store.domain.product.dto.ProductResponse;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. [어드민] 포켓몬 상자 등록 API
    @PostMapping
    public ResponseEntity<String> registerProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productService.registerProduct(
                request.getName(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getImageUrl()
        );
        return ResponseEntity.ok("상품 등록 완료! 상품 번호: " + productId);
    }

    // 2. [모두] 전체 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        
        // Entity 리스트를 DTO 리스트로 예쁘게 포장해서 반환
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
    Product product = productService.getProduct(id); // Service에 getProduct(id) 메서드가 있다고 가정
    return ResponseEntity.ok(new ProductResponse(product));
}
}