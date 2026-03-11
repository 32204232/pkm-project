package com.pkm.store.domain.cart.service;

import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.repository.CartItemRepository;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.product.entity.Product;
import com.pkm.store.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 장바구니에 포켓몬 상자 담기
     */
    @Transactional
    public Long addCart(Long memberId, Long productId, int count) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 실무 포인트: 이미 장바구니에 있는 상품이면 수량만 늘려주는 게 국룰입니다.
        // (현재는 단순화를 위해 무조건 새로 담거나 예외처리 하는 방식으로 가겠습니다. 원하시면 나중에 고도화!)
        cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .ifPresent(c -> { throw new IllegalStateException("이미 장바구니에 있는 상품입니다."); });

        CartItem cartItem = CartItem.builder()
                .member(member)
                .product(product)
                .count(count)
                .build();

        return cartItemRepository.save(cartItem).getId();
    }

    /**
     * 내 장바구니 목록 보기
     */
    public List<CartItem> getMyCart(Long memberId) {
        return cartItemRepository.findByMemberId(memberId);
    }
}