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

  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      const data = await productApi.getAllProducts();
      
      // [★핵심 방어 코드] data가 undefined로 오더라도 빈 배열([])을 넣어 에러 방지!
      setProducts(data || []); 
      setError(null);
    } catch (err: any) {
      console.error(err);
      setError('상품 목록을 불러오는 중 오류가 발생했습니다.');
      setProducts([]); // 에러 시에도 빈 배열 유지
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