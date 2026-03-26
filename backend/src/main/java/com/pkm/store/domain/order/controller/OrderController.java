package com.pkm.store.domain.order.controller;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.dto.OrderResponse;
import com.pkm.store.domain.order.entity.Order; // Order 엔티티 임포트 추가
import com.pkm.store.domain.order.service.OrderConcurrencyFacade;
import com.pkm.store.domain.order.service.OrderService; // OrderService 임포트 추가
import com.pkm.store.global.dto.ApiResponse; //
import com.pkm.store.global.exception.CustomException; //
import com.pkm.store.global.exception.ErrorCode; //
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderConcurrencyFacade orderConcurrencyFacade; //
    private final MemberRepository memberRepository; //
    // 1. OrderService 필드 주입 추가 (getOrderDetail에서 사용하기 위함)
    private final OrderService orderService; //

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(Principal principal) {
        // 현재 로그인한 사용자 확인
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 동시성 방어가 적용된 주문 서비스 호출
        Long orderId = orderConcurrencyFacade.createOrderSafely(member.getId());

        return ResponseEntity.ok(ApiResponse.success(orderId));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable Long orderId, 
            Principal principal) {
        
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 2. 주입된 orderService를 사용하여 주문 내역을 조회합니다.
        Order order = orderService.getOrder(orderId, member.getId());
        
        // 조회된 Order 엔티티를 OrderResponse DTO로 변환하여 반환합니다.
        return ResponseEntity.ok(ApiResponse.success(new OrderResponse(order)));
    }
}