package com.pkm.store.domain.product.service;

import com.pkm.store.domain.product.dto.ProductCreateRequest;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.repository.ProductRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // [★추가★] 로그 기록용
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client; // [★추가★] 삭제 기능을 위해 필요
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j // [★추가★]
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Template s3Template; 
    private final S3Client s3Client; // [★추가★]

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * [어드민 전용] 상품 등록
     */
    @Transactional
    public Long createProduct(ProductCreateRequest request, MultipartFile imageFile) {
        validateImageFile(imageFile);
        
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = uploadImageToS3(imageFile); // 업로드 로직 분리
        }

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(imageUrl)
                .category(request.getCategory())
                .series(request.getSeries())
                .status(request.getStatus())
                .releaseDate(request.getReleaseDate())
                .build();

        productRepository.save(product);
        return product.getId();
    }

    /**
     * [어드민 전용] 상품 수정
     * [개선] 새로운 이미지가 올라오면 기존 S3 파일을 삭제하여 비용을 절감합니다.
     */
    @Transactional
    public void updateProduct(Long id, ProductCreateRequest request, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));

        String imageUrl = product.getImageUrl();
        
        // 새 파일이 있는 경우에만 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            validateImageFile(imageFile);
            
            // 1. [★핵심★] 기존 이미지가 있다면 S3에서 먼저 삭제
            deleteImageFromS3(imageUrl);
            
            // 2. 새 이미지 업로드
            imageUrl = uploadImageToS3(imageFile);
        }

        product.updateProduct(
            request.getName(), 
            request.getPrice(), 
            request.getStockQuantity(), 
            imageUrl,
            request.getCategory(), 
            request.getSeries(), 
            request.getStatus(), 
            request.getReleaseDate()
        );
    }

    /**
     * [어드민 전용] 상품 삭제
     * [개선] DB 데이터 삭제 전 S3에 저장된 이미지 파일도 함께 삭제합니다.
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        
        // [★핵심★] S3 이미지 삭제
        deleteImageFromS3(product.getImageUrl());
        
        productRepository.delete(product);
    }

    /**
     * S3 파일 삭제 헬퍼 메서드
     */
    private void deleteImageFromS3(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        
        try {
            // URL에서 파일명(Key) 추출 (마지막 '/' 이후의 문자열)
            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            
            log.info("S3 파일 삭제 성공: {}", key);
        } catch (Exception e) {
            // 파일 삭제 실패가 상품 로직 자체를 중단시키지 않도록 에러 로그만 남김
            log.error("S3 파일 삭제 중 오류 발생: {}", imageUrl, e);
        }
    }

    /**
     * S3 업로드 로직 분리
     */
    private String uploadImageToS3(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;
            InputStream inputStream = imageFile.getInputStream();
            var s3Resource = s3Template.upload(bucketName, savedFilename, inputStream);
            return s3Resource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException("S3 이미지 업로드 중 오류 발생 삐까!", e);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포켓몬 상자입니다."));
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return;

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }
}