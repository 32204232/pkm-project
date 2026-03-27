// frontend/src/hooks/useOrder.ts
import { useState, useEffect } from 'react';
import { loadTossPayments } from '@tosspayments/payment-sdk';
import { orderApi, type OrderResponse } from '../api/orderApi';

// 1. 주문 및 결제 프로세스용 훅
export const useOrder = () => {
  const [isOrdering, setIsOrdering] = useState(false);

  const checkout = async () => {
    try {
      setIsOrdering(true);
      
      // 1. 우리 서버에 가주문 생성 (결제에 필요한 데이터 수신)
      // orderApi.ts에서 수정한 타입을 통해 비구조화 할당이 가능해집니다.
      const { orderUid, totalPrice, orderName } = await orderApi.createOrder();

      // 2. 토스페이먼츠 SDK 초기화
      const clientKey = 'test_ck_P9BRQmyarYDZbLvaekm7VJ07KzLN';
      const tossPayments = await loadTossPayments(clientKey);

      // 3. 결제창 띄우기
      // 성공/실패 시 이동할 URL은 window.location.origin을 써서 유연하게 대처합니다.
      await tossPayments.requestPayment('카드', {
        amount: totalPrice,
        orderId: orderUid,
        orderName: orderName,
        successUrl: `${window.location.origin}/order/success`,
        failUrl: `${window.location.origin}/order/fail`,
      });

    } catch (error) {
      console.error("결제 준비 중 에러 발생:", error);
      alert("주문 처리 중 오류가 발생했습니다. 다시 시도해주세요 삐까!");
    } finally {
      setIsOrdering(false);
    }
  };

  return { checkout, isOrdering };
};

// 2. 주문 상세(영수증) 조회용 훅
export const useOrderDetail = (orderId: string | undefined) => {
  const [orderDetail, setOrderDetail] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!orderId) return;
    
    const fetchOrder = async () => {
      try {
        const data = await orderApi.getOrderDetail(orderId);
        setOrderDetail(data);
      } catch (err) {
        console.error('Fetch order detail failed', err);
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  return { orderDetail, loading };
};