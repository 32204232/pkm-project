// backend/src/main/java/com/pkm/store/domain/order/dto/OrderCreateResponse.java (새로 생성)
package com.pkm.store.domain.order.dto;

import com.pkm.store.domain.order.entity.Order;
import lombok.Getter;

@Getter
public class OrderCreateResponse {
    private String orderUid;   // 토스 결제창 orderId용 (UUID)
    private int totalPrice;    // 검증된 총 금액
    private String orderName;  // 결제창에 뜰 상품명 (예: 피카츄 외 2건)

    public OrderCreateResponse(Order order) {
        this.orderUid = order.getOrderUid();
        this.totalPrice = order.getTotalPrice();
        // 상품명을 생성하는 로직 (첫 번째 상품명 + 외 N건)
        String firstItemName = order.getOrderItems().get(0).getProduct().getName();
        int otherCount = order.getOrderItems().size() - 1;
        this.orderName = otherCount > 0 ? firstItemName + " 외 " + otherCount + "건" : firstItemName;
    }
}