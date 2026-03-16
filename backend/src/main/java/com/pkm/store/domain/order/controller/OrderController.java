package com.pkm.store.domain.order.controller;

import com.pkm.store.domain.order.dto.OrderResponse;
import com.pkm.store.domain.order.entity.Order;
import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.service.OrderService;
import com.pkm.store.domain.order.service.OrderConcurrencyFacade; // [★추가] 동시성 방어 퍼사드 임포트
import com.pkm.store.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderConcurrencyFacade orderConcurrencyFacade; // [★추가] 안전한 락 처리를 위한 의존성 주입
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(Principal principal) {
        // 1. JWT 토큰을 통해 요청한 회원 정보 조회
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. [★핵심 수정★] 기존 orderService.createOrderFromCart() 대신 Facade 호출!
        // 이제 10,000명이 동시에 결제 버튼을 눌러도 Redis MultiLock이 줄을 세워 안전하게 재고를 깎습니다.
        Long orderId = orderConcurrencyFacade.createOrderSafely(member.getId());

        // 3. ApiResponse 규격에 맞춰 응답
        return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 완료되었습니다.", orderId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(@PathVariable("orderId") Long orderId, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 단건 조회는 재고가 깎이는 '쓰기' 작업이 아니므로 락(Lock)이 필요 없습니다.
        // 따라서 성능을 위해 퍼사드를 거치지 않고 기존 Service를 다이렉트로 호출합니다.
        Order order = orderService.getOrder(orderId, member.getId());
        
        return ResponseEntity.ok(ApiResponse.success(new OrderResponse(order)));
    }
}