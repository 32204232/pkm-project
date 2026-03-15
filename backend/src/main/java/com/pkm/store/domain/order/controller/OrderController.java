package com.pkm.store.domain.order.controller;
import com.pkm.store.domain.order.dto.OrderResponse;
import com.pkm.store.domain.order.entity.Order;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.service.OrderService;
import com.pkm.store.global.dto.ApiResponse; // [★추가]
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(Principal principal) { // [★반환형 변경]
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 전체 주문 로직 실행
        Long orderId = orderService.createOrderFromCart(member.getId());

        // [★수정] ApiResponse 규격에 맞춰 주문 번호(Long)를 담아 보냄
        return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 완료되었습니다.", orderId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@PathVariable Long orderId, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // Service에서 검증된 주문을 가져와서 DTO로 변환해 응답
        Order order = orderService.getOrder(orderId, member.getId());
        return ResponseEntity.ok(ApiResponse.success(new OrderResponse(order)));
    }
}