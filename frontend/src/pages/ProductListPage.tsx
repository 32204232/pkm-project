import React from 'react';
import ProductCard from '../components/ProductCard';
import { useProductList } from '../hooks/useProduct'; // [★핵심] 우리가 만든 훅을 가져옵니다.

const ProductListPage = () => {
  // 1. 데이터 가져오기 로직은 모두 훅에게 위임! (코드가 엄청 깔끔해집니다)
  const { products, loading, error } = useProductList();

  // 2. 안전장치: 백엔드나 네트워크 문제로 products가 undefined일 때를 대비
  const safeProducts = products || [];

  // 3. 로딩 및 에러 상태 처리
  if (loading) return <div className="py-32 text-center font-bold text-gray-500">야생의 포켓몬 상자를 찾는 중...</div>;
  if (error) return <div className="py-32 text-center font-bold text-red-500">{error}</div>;

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

      {/* 정렬 바 */}
      <div className="flex justify-between items-center mb-8 border-b pb-4 text-sm text-gray-600 font-medium">
        <div>{safeProducts.length} Items Available</div> {/* [★수정] safeProducts 사용 */}
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
      {safeProducts.length === 0 ? (
        <div className="text-center py-20 text-gray-500 font-bold bg-gray-50 rounded-2xl border-2 border-dashed border-gray-200">
          현재 입고된 포켓몬 박스가 없습니다.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {safeProducts.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ProductListPage;