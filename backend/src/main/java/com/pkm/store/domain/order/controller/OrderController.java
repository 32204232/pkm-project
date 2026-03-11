package com.pkm.store.domain.order.controller;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;

    // 장바구니 일괄 구매 (결제)
    @PostMapping
    public ResponseEntity<String> createOrder(Principal principal) {
        // 1. 토큰에서 이메일을 꺼내서 회원 번호(ID)를 찾습니다.
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 서비스에 회원 번호를 넘겨서 일괄 결제 및 재고 차감, 장바구니 비우기를 실행합니다.
        Long orderId = orderService.createOrderFromCart(member.getId());

        return ResponseEntity.ok("주문이 완료되었습니다! 주문 번호: " + orderId);
    }
}