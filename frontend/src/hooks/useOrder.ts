import { useNavigate } from 'react-router-dom';
import { orderApi } from '../api/orderApi';

export const useOrder = () => {
  const navigate = useNavigate();

  const checkout = async (cartItemIds: number[]) => {
    if (cartItemIds.length === 0) return alert('Your cart is empty.');
    
    try {
      await orderApi.createOrder(cartItemIds);
      alert('Order Placed Successfully!');
      navigate('/order-success'); // 주문 완료 페이지로 이동
    } catch (err) {
      alert('Order failed. Please try again.');
    }
  };

  return { checkout };
};