// src/api/productApi.ts
import { axiosInstance as api } from './axios';
import type { Product } from '../types/product';

export const productApi = {
  // 1. 전체 목록 조회 (언래핑 덕분에 바로 리턴)
  getAllProducts: (): Promise<Product[]> => api.get('/api/products'),

  // 2. 상세 조회
  getProductById: (id: string): Promise<Product> => api.get(`/api/products/${id}`),

  // 3. 상품 등록
  createProduct: async (productData: any, imageFile: File | null): Promise<number> => {
    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (imageFile) formData.append('image', imageFile);

    return api.post('/api/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  /**
   * 4. 상품 수정 (이미지 포함)
   * [★실무 포인트★] PUT 요청 시에도 FormData를 사용하여 이미지를 함께 보냅니다.
   */
  updateProduct: async (id: number, productData: any, imageFile: File | null): Promise<void> => {
    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (imageFile) formData.append('image', imageFile);

    return api.put(`/api/products/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  // 5. 상품 삭제
  deleteProduct: (id: number): Promise<void> => api.delete(`/api/products/${id}`)
};