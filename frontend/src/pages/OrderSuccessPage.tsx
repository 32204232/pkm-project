// frontend/src/pages/OrderSuccessPage.tsx

import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom'; // [★useNavigate 추가]
import { paymentApi } from '../api/paymentApi';
import { type OrderResponse } from '../api/orderApi';
import OrderReceipt from '../components/order/OrderReceipt';

const OrderSuccessPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate(); // [★초기화]
  const [orderData, setOrderData] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const confirm = async () => {
      const paymentKey = searchParams.get('paymentKey');
      const orderUid = searchParams.get('orderId'); // UUID 문자열
      const amount = searchParams.get('amount');

      if (paymentKey && orderUid && amount) {
        try {
          // 1. 결제 승인 요청 (이제 백엔드에서 OrderResponse를 반환함)
          const response: any = await paymentApi.confirmPayment({ 
            paymentKey, 
            orderId: orderUid, 
            amount: Number(amount) 
          });
          
          // 2. [★수정] 승인 결과로 받은 데이터를 바로 상태에 저장
          setOrderData(response); 
          
        } catch (err) {
          console.error("결제 승인 실패:", err);
          navigate('/order/fail'); // [★에러 시 실패 페이지로 이동]
        } finally {
          setLoading(false);
        }
      }
    };
    confirm();
  }, [searchParams, navigate]);

  if (loading) return <div className="p-20 text-center font-bold">결제 승인 중...</div>;

  return (
    <div className="max-w-2xl mx-auto p-10 text-center">
      {orderData ? (
        // [★수정] prop 이름 'data'가 맞는지 확인 후 적용
        <OrderReceipt data={orderData} /> 
      ) : (
        <div className="py-20 bg-gray-50 rounded-2xl">
          <p className="text-gray-500 mb-4">주문 정보를 불러올 수 없습니다.</p>
          <button onClick={() => navigate('/')} className="text-blue-600 font-bold">홈으로 가기</button>
        </div>
      )}
    </div>
  );
};

export default OrderSuccessPage;