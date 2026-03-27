package com.pkm.store.domain.order.repository;

import com.pkm.store.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // [★추가] Optional 임포트

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // [★CTO의 핵심 추가 로직]
    // Spring Data JPA가 "select * from orders where order_uid = ?" 쿼리를 알아서 짜줍니다!
    Optional<Order> findByOrderUid(String orderUid);
    List<Order> findAllByStatusAndCreatedAtBefore(Order.OrderStatus status, LocalDateTime dateTime);
}