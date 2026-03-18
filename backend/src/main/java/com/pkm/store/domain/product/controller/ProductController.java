package com.pkm.store.domain.product.controller;

import com.pkm.store.domain.product.dto.ProductCreateRequest;
import com.pkm.store.domain.product.dto.ProductResponse;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.service.ProductService;
import com.pkm.store.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. 상품 등록 (이미지 포함)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Long>> createProduct(
            @RequestPart("product") ProductCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        Long productId = productService.createProduct(request, imageFile);
        return ResponseEntity.ok(ApiResponse.success("상품 등록에 성공했습니다.", productId));
    }

    // 2. 전체 상품 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 3. 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.success(new ProductResponse(product)));
    }

    /**
     * 4. [어드민] 상품 정보 수정 (재고, 가격, 이미지 변경 포함)
     * [★30년 차 포인트★] 수정도 등록과 동일하게 Multipart 포맷으로 맞춰야 이미지 교체가 가능합니다.
     */
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        
        productService.updateProduct(id, request, imageFile); // 서비스 레이어에도 imageFile 파라미터 추가 필요
        return ResponseEntity.ok(ApiResponse.success("상품 수정이 완료되었습니다.",null));
    }

    // 5. [어드민] 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("상품이 삭제되었습니다.", null));
    }
}