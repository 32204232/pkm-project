package com.pkm.store.domain.order.repository;

import com.pkm.store.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 특정 회원의 주문 내역(로그) 조회
    List<Order> findByMemberId(Long memberId);
}