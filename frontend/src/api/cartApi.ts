// src/api/cartApi.ts
import api from './axios';
import type { CartAddRequest, CartResponse } from '../types/cart';

// [★핵심] 반드시 앞에 'export const' 가 붙어 있어야 합니다! default가 들어가면 안 됩니다.
export const cartApi = {
  addToCart: (data: CartAddRequest): Promise<void> => api.post('/api/carts', data),
  getMyCart: (): Promise<CartResponse[]> => api.get('/api/carts'),
  updateCount: (cartItemId: number, count: number): Promise<void> => api.patch(`/api/carts/${cartItemId}`, { count }),
  deleteItem: (cartItemId: number): Promise<void> => api.delete(`/api/carts/${cartItemId}`)
};