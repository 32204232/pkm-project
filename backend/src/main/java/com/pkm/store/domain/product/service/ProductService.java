package com.pkm.store.domain.product.service;

import com.pkm.store.domain.product.dto.ProductCreateRequest;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.repository.ProductRepository;
import io.awspring.cloud.s3.S3Template; // [★추가★] AWS S3 마법 지팡이
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    
    // [★추가★] S3 통신을 아주 쉽게 만들어주는 템플릿 도구
    private final S3Template s3Template; 

    // [★추가★] application.yaml에 적어둔 버킷 이름(pkm-store-image-bucket)을 쏙 빼온다.
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * [어드민 전용] 상품 등록 (AWS S3 이미지 파일 업로드 적용!)
     */
    @Transactional
    public Long createProduct(ProductCreateRequest request, MultipartFile imageFile) {
        String imageUrl = null;

        // 1. S3 이미지 업로드 로직 (이전과 동일)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String savedFilename = UUID.randomUUID() + "_" + originalFilename;
                InputStream inputStream = imageFile.getInputStream();
                var s3Resource = s3Template.upload(bucketName, savedFilename, inputStream);
                imageUrl = s3Resource.getURL().toString();
            } catch (IOException e) {
                throw new RuntimeException("S3로 이미지를 전송하는 중 오류가 발생했습니다 삐까!", e);
            }
        }

        // 2. 상품 엔티티 생성 [★수정: 새로운 4가지 데이터 추가 조립!★]
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(imageUrl)
                .category(request.getCategory())     // 추가됨!
                .series(request.getSeries())         // 추가됨!
                .status(request.getStatus())         // 추가됨!
                .releaseDate(request.getReleaseDate()) // 추가됨!
                .build();

        productRepository.save(product);
        return product.getId();
    }

    /**
     * [어드민 전용] 상품 수정 (더티 체킹 활용)
     * (Product 엔티티에 updateProduct 메서드도 파라미터가 늘어나야 한다!)
     */
    @Transactional
public void updateProduct(Long id, ProductCreateRequest request, MultipartFile imageFile) {
    // 1. 기존 상품 조회
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));

    // 2. 이미지 처리: 새 파일이 있으면 업로드 후 URL 갱신, 없으면 기존 URL 유지
    String imageUrl = product.getImageUrl(); //
    if (imageFile != null && !imageFile.isEmpty()) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String savedFilename = java.util.UUID.randomUUID() + "_" + originalFilename;
            java.io.InputStream inputStream = imageFile.getInputStream();
            var s3Resource = s3Template.upload(bucketName, savedFilename, inputStream);
            imageUrl = s3Resource.getURL().toString(); //
        } catch (java.io.IOException e) {
            throw new RuntimeException("S3 이미지 수정 중 오류 발생", e);
        }
    }

    // 3. 엔티티 업데이트 (8개 파라미터 순서 엄수)
    product.updateProduct(
        request.getName(), 
        request.getPrice(), 
        request.getStockQuantity(), 
        imageUrl, //
        request.getCategory(), 
        request.getSeries(), 
        request.getStatus(), 
        request.getReleaseDate()
    );
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