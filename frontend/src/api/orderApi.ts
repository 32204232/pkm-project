import api from './axios';

// 백엔드의 DTO와 일치하는 타입 정의
export interface OrderItemResponse {
  productName: string;
  orderPrice: number;
  count: number;
  imageUrl: string;
}

export interface OrderResponse {
  orderId: number;
  orderDate: string;
  totalPrice: number;
  orderItems: OrderItemResponse[];
}

export const orderApi = {
  createOrder: (): Promise<number> => api.post('/api/orders'),
  // 영수증 데이터 가져오기 API 추가
  getOrderDetail: (orderId: string): Promise<OrderResponse> => api.get(`/api/orders/${orderId}`)
};