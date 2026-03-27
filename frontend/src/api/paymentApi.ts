// frontend/src/api/paymentApi.ts 수정
import api from './axios';

export const paymentApi = {
  // amount 타입을 number로 변경하여 백엔드 Integer와 맞춤
  confirmPayment: (data: { paymentKey: string; orderId: string; amount: number }) => 
    api.post('/api/payments/confirm', data)
};