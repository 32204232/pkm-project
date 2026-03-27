// frontend/src/api/paymentApi.ts
import api from './axios';

export const paymentApi = {
  // 토스 결제 최종 승인 요청
  confirmPayment: (data: { paymentKey: string; orderId: string; amount: string }) => 
    api.post('/api/payments/confirm', data)
};