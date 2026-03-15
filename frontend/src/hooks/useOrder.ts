import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderApi, type OrderResponse } from '../api/orderApi';

// 1. 주문 생성용 훅 (수정)
export const useOrder = () => {
  const navigate = useNavigate();
  const [isOrdering, setIsOrdering] = useState(false);

  const checkout = async () => {
    try {
      setIsOrdering(true);
      const orderId = await orderApi.createOrder();
      // 성공 시 URL에 주문번호를 넣어서 이동
      navigate(`/order/success/${orderId}`); 
    } catch (err) {
      console.error('Order failed', err);
    } finally {
      setIsOrdering(false);
    }
  };

  return { checkout, isOrdering };
};

// 2. 주문 상세(영수증) 조회용 훅 (신규)
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