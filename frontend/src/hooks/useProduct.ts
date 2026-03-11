import { useState, useEffect } from 'react';
import { productApi } from '../api/productApi'; // 우리가 만든 서비스 레이어 사용
import type { Product } from '../types/product';

// 1. 특정 상품 상세 정보를 관리하는 훅 (이름을 useProduct로 통일!)
export const useProduct = (id: string | undefined) => {
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    
    setLoading(true);
    productApi.getProductById(id)
      .then((data) => {
        setProduct(data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to fetch product details.');
      })
      .finally(() => setLoading(false));
  }, [id]);

  return { product, loading, error };
};

// 2. 전체 상품 목록을 관리하는 훅
export const useProductList = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // 여기도 나중에 productApi.getAllProducts() 처럼 만들어서 교체하면 더 깔끔합니다!
    productApi.getAllProducts() 
      .then((data) => {
        setProducts(data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to fetch product list.');
      })
      .finally(() => setLoading(false));
  }, []);

  return { products, loading, error };
};