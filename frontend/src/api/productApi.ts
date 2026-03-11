import api from './axios'; //
import type { Product } from '../types/product'; //

export const productApi = {
  // 1. 전체 상품 목록 가져오기
  getAllProducts: async () => {
    const response = await api.get<Product[]>('/api/products'); //
    return response.data;
  },

  // 2. 특정 상품 상세 정보 가져오기
  getProductById: async (id: string) => {
    const response = await api.get<Product>(`/api/products/${id}`); //
    return response.data;
  }, // <--- 여기에 쉼표(,)가 꼭 필요합니다!

  // 3. 상품 등록 (관리자용)
  registerProduct: async (productData: any) => {
    return await api.post('/api/products', productData); //
  },

  // 4. 상품 수정 (재고 변경 등)
  updateProduct: async (id: number, productData: any) => {
    return await api.put(`/api/products/${id}`, productData); //
  },

  // 5. 상품 삭제
  deleteProduct: async (id: number) => {
    return await api.delete(`/api/products/${id}`); //
  }
};