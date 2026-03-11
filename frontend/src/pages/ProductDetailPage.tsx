import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useProduct } from '../hooks/useProduct';
import MainLayout from '../components/layout/MainLayout'; // Layout 적용

const ProductDetailPage = () => {
  const { id } = useParams();
  const { product, loading, error } = useProduct(id);
  
  // [수정] 수량 상태 추가
  const [quantity, setQuantity] = useState(1);

  if (loading) return <MainLayout><div className="p-20 text-center font-bold">Loading...</div></MainLayout>;
  if (error) return <MainLayout><div className="p-20 text-center text-red-500">{error}</div></MainLayout>;
  if (!product) return <MainLayout><div className="p-20 text-center">Product not found.</div></MainLayout>;

  return (
    <MainLayout>
      <div className="py-6 lg:py-10">
        <div className="flex flex-col lg:flex-row gap-12">
          
          {/* 왼쪽: 상품 이미지 */}
          <div className="flex-1 bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
            <img 
              src={product.imageUrl} 
              alt={product.name} 
              className="w-full h-auto object-contain max-h-[500px]"
            />
          </div>

          {/* 오른쪽: 구매 정보 */}
          <div className="flex-1 space-y-8">
            <div>
              <nav className="flex text-sm text-gray-400 mb-4">
                <span>Home</span> <span className="mx-2">&gt;</span> <span>Booster Packs</span>
              </nav>
              <h1 className="text-4xl font-black text-gray-900 leading-tight mb-2">
                {product.name}
              </h1>
              <p className="text-blue-600 font-bold tracking-widest text-sm uppercase">Korean Version - Official Sealed</p>
            </div>

            <div className="flex items-end gap-3">
              <span className="text-5xl font-black text-red-600">${product.price}</span>
              <span className="text-gray-400 mb-2 font-medium uppercase text-sm">usd</span>
            </div>

            <div className="border-t border-b border-gray-100 py-6 space-y-4">
              <div className="flex items-center text-sm text-gray-600">
                <span className="w-32 font-bold">Condition</span>
                <span className="text-green-600 font-bold">New / Factory Sealed</span>
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <span className="w-32 font-bold">Shipping</span>
                <span>Ships worldwide from South Korea</span>
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <span className="w-32 font-bold">Quantity</span>
                <div className="flex items-center border rounded-lg overflow-hidden bg-white">
                  <button 
                    onClick={() => setQuantity(Math.max(1, quantity - 1))} 
                    className="px-4 py-2 hover:bg-gray-100 border-r transition-colors"
                  > - </button>
                  <span className="px-6 py-2 font-bold min-w-[50px] text-center">{quantity}</span>
                  <button 
                    onClick={() => setQuantity(quantity + 1)} 
                    className="px-4 py-2 hover:bg-gray-100 border-l transition-colors"
                  > + </button>
                </div>
              </div>
            </div>

            <div className="flex gap-4">
              <button className="flex-1 bg-black text-white py-4 rounded-xl font-black text-lg hover:bg-gray-800 transition-all active:scale-[0.98] shadow-lg">
                ADD TO CART
              </button>
              <button className="p-4 border border-gray-200 rounded-xl hover:bg-gray-50 transition-all text-gray-400 hover:text-red-500">
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </button>
            </div>

            {/* 신뢰 배지 */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="flex items-center gap-3 p-4 bg-blue-50 rounded-xl">
                <span className="text-2xl">🛡️</span>
                <div>
                  <p className="text-xs font-black text-blue-800 uppercase">Authenticity</p>
                  <p className="text-[10px] text-blue-600">100% Genuine Korean Product</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-4 bg-orange-50 rounded-xl">
                <span className="text-2xl">✈️</span>
                <div>
                  <p className="text-xs font-black text-orange-800 uppercase">Worldwide</p>
                  <p className="text-[10px] text-orange-600">Express International Shipping</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default ProductDetailPage;