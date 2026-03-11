import api from './axios';

export const orderApi = {
  // 주문 생성 (백엔드 @PostMapping("/api/orders"))
  // 장바구니에 담긴걸 한꺼번에 주문한다고 가정
  createOrder: async (cartItemIds: number[]) => {
    const response = await api.post('/api/orders', { cartItemIds });
    return response.data;
  }
};