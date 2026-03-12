import api from './axios';

export const orderApi = {
  // 백엔드가 Body를 받지 않으므로 인자 없이 호출하도록 수정
  createOrder: (): Promise<number> => api.post('/api/orders')
};