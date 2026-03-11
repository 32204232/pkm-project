package com.pkm.store.domain.product.repository;

import com.pkm.store.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 기본적인 저장(save), 조회(findById, findAll) 등은 JpaRepository가 알아서 다 해줍니다.
    // 당장 추가할 특수 쿼리는 없으니 뼈대만 둡니다.
}