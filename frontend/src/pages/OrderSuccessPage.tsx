import React from 'react';
import { Link, useParams } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';
import { useOrderDetail } from '../hooks/useOrder';
import OrderReceipt from '../components/order/OrderReceipt';

const OrderSuccessPage = () => {
  // URL에서 :orderId 값을 뽑아옵니다.
  const { orderId } = useParams<{ orderId: string }>();
  const { orderDetail, loading } = useOrderDetail(orderId);

  if (loading) return <MainLayout><div className="p-20 text-center font-bold">영수증을 인쇄하는 중...</div></MainLayout>;
  if (!orderDetail) return <MainLayout><div className="p-20 text-center font-bold text-red-500">주문 내역을 찾을 수 없습니다.</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-2xl mx-auto text-center py-10">
        <div className="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6 text-4xl">
          ✓
        </div>
        <h1 className="text-3xl font-black mb-2 uppercase italic">Thank You!</h1>
        <p className="text-gray-500 mb-2">Your order #{orderDetail.orderId} has been placed.</p>
        <p className="text-sm text-gray-400 mb-4">{new Date(orderDetail.orderDate).toLocaleString()}</p>

        {/* 우리가 만든 영수증 컴포넌트 삽입 */}
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