import React from 'react';
import { Link } from 'react-router-dom'; // [추가] 이동을 위한 링크 컴포넌트
import type { Product } from '../types/product';

interface Props {
  product: Product;
}

const ProductCard = ({ product }: Props) => {
  const isSoldOut = product.stockQuantity === 0;

  // 찜하기 버튼 클릭 시 상세페이지로 이동하는 것을 방지하는 함수
  const handleWishlistClick = (e: React.MouseEvent) => {
    e.preventDefault(); // 부모 Link의 이동 이벤트를 취소
    e.stopPropagation(); // 이벤트가 위로 퍼지는 것을 차단
    alert('Added to Wishlist! (Feature coming soon)');
  };

  return (
    /* 전체 카드를 Link로 감싸서 어디를 눌러도 상세페이지로 이동하게 합니다. */
    <Link 
      to={`/product/${product.id}`} 
      // [보완] 품절 시 opacity-60을 추가하여 흐리게 보이게 합니다.
      className={`group relative flex flex-col bg-white rounded-lg transition-all duration-300 hover:shadow-xl ${isSoldOut ? 'opacity-60' : 'hover:-translate-y-1'}`}
    >
      <div className="relative aspect-square overflow-hidden rounded-t-lg bg-gray-100">
        <img
          src={product.imageUrl || 'https://via.placeholder.com/300'}
          alt={product.name}
          // [유지] 기존 회색조 처리
          className={`w-full h-full object-contain p-4 transition-transform duration-500 group-hover:scale-110 ${isSoldOut ? 'grayscale' : ''}`}
        />
        
        {isSoldOut && (
          <div className="absolute inset-0 bg-black/20 flex items-center justify-center z-10">
            <span className="text-white font-black text-xl tracking-tighter border-2 border-white px-3 py-1 rotate-[-10deg]">
              OUT OF STOCK
            </span>
          </div>
        )}

        {/* 찜하기(하트) 버튼 - 클릭 이벤트 분리됨 */}
        <button 
          onClick={handleWishlistClick}
          className="absolute top-3 right-3 z-20 p-2 bg-white/80 rounded-full hover:bg-white shadow-sm hover:scale-110 transition-transform"
        >
          <svg className="w-5 h-5 text-gray-400 hover:text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </button>
      </div>

      {/* 정보 영역 */}
      <div className="p-4 text-center">
        {/* 카테고리 (없으면 기본값 표시) */}
        <p className="text-[10px] text-blue-500 font-bold mb-1 uppercase tracking-widest">
          {product.category || 'Booster Pack'}
        </p>
        <h3 className="text-sm font-semibold text-gray-800 line-clamp-2 h-10 mb-2 group-hover:text-blue-600 transition-colors">
          {product.name}
        </h3>
        <p className="text-lg font-black text-red-600">
          ${product.price.toLocaleString()}
        </p>
      </div>
    </Link>
  );
};

export default ProductCard;