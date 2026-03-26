package com.pkm.store.domain.cart.controller;

import com.pkm.store.domain.cart.dto.CartAddRequest;
import com.pkm.store.domain.cart.dto.CartResponse;
import com.pkm.store.domain.cart.dto.UpdateCountRequest;
import com.pkm.store.domain.cart.entity.CartItem;
import com.pkm.store.domain.cart.service.CartService;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.global.dto.ApiResponse; //

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
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addCart(@Valid @RequestBody CartAddRequest request, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        cartService.addCart(member.getId(), request.getProductId(), request.getCount());
        
        // [수정] ApiResponse.success를 사용하여 규격에 맞춰 응답합니다. 데이터가 없으므로 Void 처리.
        return ResponseEntity.ok(ApiResponse.success("장바구니에 포켓몬 상자가 담겼습니다!", null));
    }

    // 2. 내 장바구니 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartResponse>>> getMyCart(Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<CartItem> cartItems = cartService.getMyCart(member.getId());
        List<CartResponse> response = cartItems.stream()
                .map(CartResponse::new)
                .collect(Collectors.toList());

        // [수정] List<CartResponse>를 ApiResponse의 data 필드에 담아서 보냅니다.
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 수량 수정 엔드포인트 예시
@PatchMapping("/{cartItemId}")
public ResponseEntity<ApiResponse<Void>> updateCount(@PathVariable Long cartItemId, @RequestBody UpdateCountRequest request) {
    cartService.updateCount(cartItemId, request.getCount());
    return ResponseEntity.ok(ApiResponse.success("수량이 변경되었습니다.", null));
}

// 삭제 엔드포인트 예시
@DeleteMapping("/{cartItemId}")
public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long cartItemId) {
    cartService.deleteItem(cartItemId);
    return ResponseEntity.ok(ApiResponse.success("장바구니에서 삭제되었습니다.", null));
}
}