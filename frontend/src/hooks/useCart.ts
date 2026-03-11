import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartApi } from '../api/cartApi';
import { useAuthStore } from '../store/authStore';

export const useCart = () => {
  const [loading, setLoading] = useState(false);
  const { isLoggedIn } = useAuthStore(); // 로그인 여부 확인
  const navigate = useNavigate();

  const addToCart = async (productId: number, count: number) => {
    // 1. 로그인 체크 (글로벌 타겟이니 영어로!)
    if (!isLoggedIn) {
      alert('Please login first to use the cart.');
      navigate('/login');
      return;
    }

    setLoading(true);
    try {
      // 2. API 호출
      await cartApi.addToCart({ productId, count });
      
      // 3. 성공 알림 및 이동 여부 확인
      if (window.confirm('Successfully added! Do you want to go to the cart?')) {
        navigate('/cart'); // 나중에 만들 장바구니 페이지로 이동
      }
    } catch (error: any) {
      console.error('Cart Error:', error);
      alert('Failed to add item. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return { addToCart, loading };
};