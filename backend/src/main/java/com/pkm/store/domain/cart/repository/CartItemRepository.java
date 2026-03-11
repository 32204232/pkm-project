package com.pkm.store.domain.cart.repository;

import com.pkm.store.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // 특정 회원의 장바구니 목록 조회
    List<CartItem> findByMemberId(Long memberId);
    
    // 이미 장바구니에 같은 상품이 있는지 확인용
    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);
    
    // 결제 완료 후 회원의 장바구니 싹 비우기
    void deleteByMemberId(Long memberId);
}