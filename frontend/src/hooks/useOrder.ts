import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderApi } from '../api/orderApi';

export const useOrder = () => {
  const navigate = useNavigate();
  const [isOrdering, setIsOrdering] = useState(false);

  const checkout = async () => {
    try {
      setIsOrdering(true);
      await orderApi.createOrder();
      alert('주문이 성공적으로 완료되었습니다!');
      navigate('/order-success');
    } catch (err) {
      console.error('Order failed', err);
    } finally {
      setIsOrdering(false);
    }
  };

  return { checkout, isOrdering };
};