package com.pkm.store.domain.product.controller;

import com.pkm.store.domain.product.dto.ProductCreateRequest;
import com.pkm.store.domain.product.dto.ProductResponse;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. [어드민] 상품 등록 (AWS S3 이미지 업로드 적용 버전!)
    // (consumes 설정을 통해 JSON 데이터와 이미지 파일을 동시에 받는다)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createProduct(
            @RequestPart("product") ProductCreateRequest request, // 상품 정보 (JSON)
            @RequestPart(value = "image", required = false) MultipartFile imageFile // 사진 파일
    ) {
        Long productId = productService.createProduct(request, imageFile);
        return ResponseEntity.ok(productId);
    }
    
    // ❌ 구버전 registerProduct 메서드는 삭제했다! (중복 라우팅 에러 방지)

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

    // 4. [어드민] 상품 정보 수정 (재고, 가격 변경 등)
    // 💡 (점장 코멘트: 나중에 이미지도 수정하게 하려면 이 녀석도 createProduct처럼 @RequestPart로 바꿔야 한다!)
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductCreateRequest request) {
        
        // 이렇게 깔끔하게 통째로 넘겨주시면 됩니다!
        productService.updateProduct(id, request);
        return ResponseEntity.ok("상품 수정이 완료되었습니다.");
    }

    // 5. [어드민] 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }
}