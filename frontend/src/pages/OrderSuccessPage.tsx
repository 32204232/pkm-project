import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import { orderApi, type OrderResponse } from '../api/orderApi';
import OrderReceipt from '../components/order/OrderReceipt';
import { paymentApi } from '../api/paymentApi';

const OrderSuccessPage = () => {
  const [searchParams] = useSearchParams();
  const [orderDetail, setOrderDetail] = useState<OrderResponse | null>(null);
  const [isConfirming, setIsConfirming] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // 1. 토스가 보낸 쿼리 파라미터 추출
    const paymentKey = searchParams.get('paymentKey');
    const orderId = searchParams.get('orderId'); // 우리 서버의 orderUid
    const amount = searchParams.get('amount');

    if (!paymentKey || !orderId || !amount) {
      setError("결제 정보가 올바르지 않습니다.");
      setIsConfirming(false);
      return;
    }

    const confirmPayment = async () => {
      try {
        // 2. 백엔드에 최종 결제 승인 요청 (이때 DB 상태가 COMPLETED로 바뀜)
       await paymentApi.confirmPayment({ paymentKey, orderId, amount });
        
        // 3. 승인 성공 후 실제 주문 상세 내역(영수증) 조회
        const data = await orderApi.getOrderDetail(orderId);
        setOrderDetail(data);
      } catch (err) {
        console.error('Payment confirmation failed', err);
        setError("결제 승인 처리 중 오류가 발생했습니다 삐까!");
      } finally {
        setIsConfirming(false);
      }
    };

    confirmPayment();
  }, [searchParams]);

  if (isConfirming) return <MainLayout><div className="p-20 text-center font-bold">결제 승인을 요청 중입니다...</div></MainLayout>;
  if (error) return <MainLayout><div className="p-20 text-center font-bold text-red-500">{error}</div></MainLayout>;
  if (!orderDetail) return <MainLayout><div className="p-20 text-center font-bold text-red-500">주문 내역을 찾을 수 없습니다.</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto text-center py-10">
        <div className="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6 text-4xl">✓</div>
        <h1 className="text-3xl font-black mb-2 uppercase italic">Payment Success!</h1>
        <p className="text-gray-500 mb-2">주문번호 #{orderDetail.orderId} 결제가 완료되었습니다.</p>
        
        <OrderReceipt data={orderDetail} />

        <div className="mt-10 space-y-4 max-w-md mx-auto">
          <Link to="/" className="block w-full bg-black text-white py-4 rounded-xl font-black hover:bg-gray-800 transition-all shadow-md">
            CONTINUE SHOPPING
          </Link>
        </div>
      </div>
    </MainLayout>
  );
};

export default OrderSuccessPage;