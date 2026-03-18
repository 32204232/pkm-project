package com.pkm.store.domain.order.controller;

import com.pkm.store.domain.member.entity.Member;
import com.pkm.store.domain.member.repository.MemberRepository;
import com.pkm.store.domain.order.service.OrderConcurrencyFacade;
import com.pkm.store.global.dto.ApiResponse;
import com.pkm.store.global.exception.CustomException;
import com.pkm.store.global.exception.ErrorCode;
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

    private final OrderConcurrencyFacade orderConcurrencyFacade;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(Principal principal) {
        // 1. 현재 로그인한 사용자 확인
        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 동시성 방어가 적용된 주문 서비스 호출
        Long orderId = orderConcurrencyFacade.createOrderSafely(member.getId());

        return ResponseEntity.ok(ApiResponse.success(orderId));
    }
}