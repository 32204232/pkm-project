import { useState, useEffect } from 'react';
import { cartApi } from '../api/cartApi';
import type { CartResponse } from '../types/cart';

export const useCartList = () => {
  const [items, setItems] = useState<CartResponse[]>([]);
  const [loading, setLoading] = useState(true);

  // 1. 목록 불러오기
  const fetchCart = async () => {
    try {
      setLoading(true);
      const data = await cartApi.getMyCart();
      setItems(data);
    } catch (err) {
      console.error('Fetch cart failed', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCart(); }, []);

  // 2. 수량 변경 로직
  const updateQuantity = async (cartItemId: number, newCount: number) => {
    if (newCount < 1) return;
    await cartApi.updateCount(cartItemId, newCount);
    setItems(items.map(item => item.cartItemId === cartItemId ? { ...item, count: newCount } : item));
  };

  // 3. 삭제 로직
  const removeItem = async (cartItemId: number) => {
    await cartApi.deleteItem(cartItemId);
    setItems(items.filter(item => item.cartItemId !== cartItemId));
  };

  // 4. 총합 계산 (전체 합계 금액)
  const totalPrice = items.reduce((acc, item) => acc + (item.price * item.count), 0);

  return { items, loading, updateQuantity, removeItem, totalPrice, refresh: fetchCart };
};