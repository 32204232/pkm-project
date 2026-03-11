package com.pkm.store.domain.cart.controller;

import com.pkm.store.domain.cart.dto.CartAddRequest;
import com.pkm.store.domain.cart.dto.CartResponse;
import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.service.CartService;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberRepository memberRepository;

    // 1. 장바구니 담기
    // Principal 객체에는 JWT 필터를 통과한 사용자의 이메일이 들어있습니다!
    @PostMapping
    public ResponseEntity<String> addCart(@Valid @RequestBody CartAddRequest request, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        cartService.addCart(member.getId(), request.getProductId(), request.getCount());
        return ResponseEntity.ok("장바구니에 포켓몬 상자가 담겼습니다!");
    }

    // 2. 내 장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartResponse>> getMyCart(Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<CartItem> cartItems = cartService.getMyCart(member.getId());
        List<CartResponse> response = cartItems.stream()
                .map(CartResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}