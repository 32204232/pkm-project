// frontend/src/hooks/useOrder.ts
import { useState } from 'react';
import { orderApi } from '../api/orderApi';
import { loadTossPayments } from '@tosspayments/payment-sdk';

// frontend/src/hooks/useOrder.ts 수정

// frontend/src/hooks/useOrder.ts

export const useOrder = () => {
  const [isOrdering, setIsOrdering] = useState(false);

  const checkout = async (orderName: string) => {
    setIsOrdering(true);
    try {
      // 1. 주문 생성 (백엔드에서 숫자 ID만 반환 중인 경우 대비)
      const orderId = await orderApi.createOrder(); 
      
      // 2. 상세 정보 다시 조회 (orderUid, totalPrice를 얻기 위해)
      const res: any = await orderApi.getOrder(Number(orderId));

      const clientKey = "test_ck_D5mORe5J8Yq0P6vD06bR3u0Y9W6P";
      const tossPayments = await loadTossPayments(clientKey);

      await tossPayments.requestPayment('카드', {
        amount: res.totalPrice, 
        orderId: res.orderUid,  
        orderName: orderName, // OrderResponse에 없으므로 파라미터로 받은 값을 그대로 사용
        successUrl: `${window.location.origin}/order/success`,
        failUrl: `${window.location.origin}/order/fail`,
      });
    } catch (error) {
      alert("주문 처리 중 오류가 발생했습니다.");
    } finally {
      setIsOrdering(false);
    }
  };

  return { checkout, isOrdering };
};