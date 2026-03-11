import api from './axios';
import type { CartAddRequest, CartResponse } from '../types/cart';

export const cartApi = {
  // 장바구니 담기
  addToCart: async (data: CartAddRequest) => {
    const response = await api.post<string>('/api/carts', data);
    return response.data;
  },

  // 장바구니 목록 조회
  getMyCart: async () => {
    const response = await api.get<CartResponse[]>('/api/carts');
    return response.data;
  },

  // 수량 변경 (백엔드 @PatchMapping("/api/carts/{cartItemId}"))
  updateCount: async (cartItemId: number, count: number) => {
    const response = await api.patch(`/api/carts/${cartItemId}`, { count });
    return response.data;
  },

  // 아이템 삭제 (백엔드 @DeleteMapping("/api/carts/{cartItemId}"))
  deleteItem: async (cartItemId: number) => {
    const response = await api.delete(`/api/carts/${cartItemId}`);
    return response.data;
  }
};