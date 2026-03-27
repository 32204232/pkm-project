// frontend/src/api/orderApi.ts
import api from './axios';

// [★수정★] 주문 생성 시 토스 결제에 필요한 정보들을 받기 위한 타입
export interface OrderCreateResponse {
  orderUid: string;  // 서버에서 생성한 UUID (토스의 orderId로 사용)
  totalPrice: number; // 총 결제 금액
  orderName: string;  // 주문 명 (예: "피카츄 박스 외 2건")
}

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
  // [★수정★] 이제 number가 아닌 OrderCreateResponse 객체를 반환합니다.
  createOrder: (): Promise<OrderCreateResponse> => api.post('/api/orders'),
  getOrderDetail: (orderId: string): Promise<OrderResponse> => api.get(`/api/orders/${orderId}`)
};