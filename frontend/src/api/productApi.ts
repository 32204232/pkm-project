import api from './axios';
import type { Product } from '../types/product';

export const productApi = {
  // 1. 전체 상품 목록 가져오기 (추가됨!)
  getAllProducts: async () => {
    // 백엔드: @GetMapping("/api/products")
    const response = await api.get<Product[]>('/api/products');
    return response.data;
  },

  // 2. 특정 상품 상세 정보 가져오기
  getProductById: async (id: string) => {
    // 백엔드: @GetMapping("/api/products/{id}")
    const response = await api.get<Product>(`/api/products/${id}`);
    return response.data;
  }
};