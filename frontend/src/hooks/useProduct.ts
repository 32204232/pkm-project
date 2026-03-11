import { useState, useEffect, useCallback } from 'react';
import { productApi } from '../api/productApi';
import type { Product } from '../types/product';

// 1. 특정 상품 상세 정보를 관리하는 훅
export const useProduct = (id: string | undefined) => {
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const data = await productApi.getProductById(id);
        setProduct(data);
        setError(null);
      } catch (err: any) {
        const errMsg = '상품 상세 정보를 가져오는데 실패했습니다.';
        console.error(err);
        setError(errMsg);
        // 사용자에게 직접적인 피드백 제공
        alert(errMsg);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  return { product, loading, error };
};

// 2. 전체 상품 목록을 관리하는 훅
export const useProductList = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 로직을 함수로 분리하여 나중에 '새로고침' 기능을 구현할 수 있게 함
  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      const data = await productApi.getAllProducts();
      setProducts(data);
      setError(null);
    } catch (err: any) {
      const errMsg = '상품 목록을 불러오는 중 오류가 발생했습니다.';
      console.error(err);
      setError(errMsg);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  return { 
    products, 
    loading, 
    error, 
    refresh: fetchProducts // 나중에 관리자 페이지에서 등록 후 목록 갱신할 때 사용!
  };
};