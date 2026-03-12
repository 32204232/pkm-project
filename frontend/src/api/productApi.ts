import axiosInstance from './axios';
import type { Product } from '../types/product';

export const productApi = {
  // 1. 전체 상품 목록 가져오기 (이 녀석이 없어서 에러가 났던 거다!)
  getAllProducts: async (): Promise<Product[]> => {
    const response = await axiosInstance.get('/api/products');
    return response.data;
  },

  // 2. 단일 상품 상세 정보 가져오기
  getProductById: async (id: string): Promise<Product> => {
    const response = await axiosInstance.get(`/api/products/${id}`);
    return response.data;
  },

  // 3. [관리자 전용] 새 포켓몬 상자 등록 (우리가 아까 만든 녀석)
  createProduct: async (productData: any, imageFile: File | null) => {
    const formData = new FormData();
    
    // JSON 데이터를 Blob으로 감싸서 넣기
    formData.append(
      'product', 
      new Blob([JSON.stringify(productData)], { type: 'application/json' })
    );

    // 이미지 파일이 있으면 넣기
    if (imageFile) {
      formData.append('image', imageFile);
    }

    // 서버로 발송! (관리자 권한 토큰은 axiosInstance가 알아서 넣어줄 거다)
    const response = await axiosInstance.post('/api/products', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // 4. [관리자 전용] 포켓몬 상자 삭제 (AdminPage에서 나중에 쓸 녀석)
  deleteProduct: async (id: number) => {
    const response = await axiosInstance.delete(`/api/products/${id}`);
    return response.data;
  }
};