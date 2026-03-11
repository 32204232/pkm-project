import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useProduct } from '../hooks/useProduct';
import { useCart } from '../hooks/useCart';
import MainLayout from '../components/layout/MainLayout';
// 부품 불러오기
import ProductImage from '../components/product/detail/ProductImage';
import ProductInfo from '../components/product/detail/ProductInfo';
import ProductActions from '../components/product/detail/ProductActions';
import TrustBadges from '../components/product/detail/TrustBadges';

const ProductDetailPage = () => {
  const { id } = useParams();
  const { product, loading, error } = useProduct(id);
  const [quantity, setQuantity] = useState(1);
  const { addToCart, loading: cartLoading } = useCart();

  const isOutOfStock = product?.stockQuantity === 0;

  if (loading) return <MainLayout><div className="p-20 text-center font-bold text-gray-400">Loading Product...</div></MainLayout>;
  if (error || !product) return <MainLayout><div className="p-20 text-center text-red-500 font-bold">Product not found.</div></MainLayout>;

  return (
    <MainLayout>
      <div className="py-6 lg:py-10">
        <div className="flex flex-col lg:flex-row gap-12">
          {/* 1. 왼쪽 이미지 영역 */}
          <ProductImage src={product.imageUrl} alt={product.name} />

          {/* 2. 오른쪽 정보 및 구매 영역 */}
          <div className="flex-1 space-y-8">
            <ProductInfo name={product.name} price={product.price} />
            
            <ProductActions 
              quantity={quantity} 
              setQuantity={setQuantity}
              isOutOfStock={isOutOfStock}
              onAddToCart={() => addToCart(product.id, quantity)}
              cartLoading={cartLoading}
            />

            <TrustBadges />
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default ProductDetailPage;