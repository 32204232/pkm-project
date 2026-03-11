import React, { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import type { Product } from '../types/product';
import api from '../api/axios';

const ProductListPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 백엔드에서 상품 목록 가져오기
    api.get('/api/products')
      .then(res => {
        setProducts(res.data);
        setLoading(res.data.length === 0); // 데이터가 없으면 로딩 상태 유지(테스트용)
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      {/* 상단 헤더 영역 */}
      <div className="flex flex-col items-center mb-12">
        <div className="flex items-center gap-2 mb-2">
          <div className="w-8 h-8 bg-red-600 rounded-full border-4 border-black relative overflow-hidden before:content-[''] before:absolute before:w-full before:h-[2px] before:bg-black before:top-1/2 after:content-[''] after:absolute after:w-2 after:h-2 after:bg-white after:border-2 after:border-black after:rounded-full after:top-1/2 after:left-1/2 after:-translate-x-1/2 after:-translate-y-1/2"></div>
          <h1 className="text-4xl font-black italic tracking-tighter">BOOSTER PACKS</h1>
        </div>
        <p className="text-gray-500">Authentic Korean Version Pokémon Cards</p>
      </div>

      {/* 정렬 바 (이미지 참고) */}
      <div className="flex justify-between items-center mb-8 border-b pb-4 text-sm text-gray-600 font-medium">
        <div>{products.length} Items Available</div>
        <div className="flex gap-4">
          <button className="hover:text-black">Popularity</button>
          <button className="hover:text-black">Low Price</button>
          <button className="hover:text-black">High Price</button>
          <select className="bg-transparent outline-none">
            <option>20 Per Page</option>
          </select>
        </div>
      </div>

      {/* 상품 그리드 (4열 레이아웃) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
        {products.map(product => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
};

export default ProductListPage;