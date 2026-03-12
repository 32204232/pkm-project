// src/api/productApi.ts
import api from './axios';
import type { Product } from '../types/product';

export const productApi = {
  // 이제 인터셉터가 data만 주므로 바로 Product[] 타입을 반환합니다.
  getAllProducts: (): Promise<Product[]> => api.get('/api/products'),

  getProductById: (id: string): Promise<Product> => api.get(`/api/products/${id}`),

  createProduct: async (productData: any, imageFile: File | null) => {
    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (imageFile) formData.append('image', imageFile);

    return api.post('/api/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  }
};