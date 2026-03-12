import React from 'react';
import { useCartList } from '../hooks/useCartList';
import { useOrder } from '../hooks/useOrder';
import MainLayout from '../components/layout/MainLayout';
import CartItem from '../components/cart/CartItem';
import CartSummary from '../components/cart/CartSummary';
import { Link } from 'react-router-dom';

const CartPage = () => {
  const { items, loading, updateQuantity, removeItem, totalPrice } = useCartList();
  const { checkout, isOrdering } = useOrder(); // isOrdering 추가

  if (loading) return <MainLayout><div className="p-20 text-center">Loading...</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-black mb-8 italic">Shopping Cart</h1>
        
        {items.length === 0 ? (
          <EmptyCartView />
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              {items.map((item) => (
                <CartItem 
                  key={item.cartItemId} 
                  item={item} 
                  onUpdate={updateQuantity} 
                  onRemove={removeItem} 
                />
              ))}
            </div>

            <div className="lg:col-span-1">
              <CartSummary 
                totalPrice={totalPrice} 
                // [수정] 이제 checkout에 인자를 넘기지 않습니다.
                onCheckout={checkout} 
                isLoading={isOrdering}
              />
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

const EmptyCartView = () => (
  <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-gray-300">
    <p className="text-gray-400 mb-6">Your cart is empty.</p>
    <Link to="/" className="bg-blue-600 text-white px-8 py-3 rounded-xl font-bold">Go Shopping</Link>
  </div>
);

export default CartPage;