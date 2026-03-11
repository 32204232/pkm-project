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

    // 1. [어드민] 상품 등록
    @PostMapping
    public ResponseEntity<String> registerProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productService.registerProduct(
                request.getName(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getImageUrl()
        );
        return ResponseEntity.ok("상품 등록 완료! 번호: " + productId);
    }

    // 2. 전체 상품 조회
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 3. 개별 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(new ProductResponse(product));
    }

    // 4. [어드민] 상품 정보 수정 (재고 변경 등) [추가]
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductCreateRequest request) {
        
        productService.updateProduct(
                id,
                request.getName(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getImageUrl()
        );
        return ResponseEntity.ok("상품 수정이 완료되었습니다.");
    }

    // 5. [어드민] 상품 삭제 [추가]
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }
}