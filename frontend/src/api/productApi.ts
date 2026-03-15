import api from './axios';
import type { Product } from '../types/product';

export const productApi = {
  // ❌ 수정 전 (에러 유발): 
  // getAllProducts: async () => { const res = await api.get('/api/products'); return res.data; }
  
  // ✅ 수정 후 (배관 직결):
  getAllProducts: (): Promise<Product[]> => api.get('/api/products'),

  getProductById: (id: string): Promise<Product> => api.get(`/api/products/${id}`),

  createProduct: async (productData: any, imageFile: File | null) => {
    const formData = new FormData();
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (imageFile) formData.append('image', imageFile);

    return api.post('/api/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  deleteProduct: (id: number): Promise<void> => api.delete(`/api/products/${id}`)
};