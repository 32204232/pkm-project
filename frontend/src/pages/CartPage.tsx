import React from 'react';
import { useCartList } from '../hooks/useCartList';
import { useOrder } from '../hooks/useOrder';
import MainLayout from '../components/layout/MainLayout';
import { Link } from 'react-router-dom';

const CartPage = () => {
  const { items, loading, updateQuantity, removeItem, totalPrice } = useCartList();
  const { checkout } = useOrder();

  if (loading) return <MainLayout><div className="p-20 text-center">Loading your cart...</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-black mb-8 uppercase tracking-tighter italic">Shopping Cart</h1>
        
        {items.length === 0 ? (
          <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-gray-300">
            <p className="text-gray-400 mb-6">Your cart is empty.</p>
            <Link to="/" className="inline-block bg-blue-600 text-white px-8 py-3 rounded-xl font-bold hover:bg-blue-700 transition-colors">
              Go Shopping
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* 아이템 리스트 (2/3 영역) */}
            <div className="lg:col-span-2 space-y-4">
              {items.map((item) => (
                <div key={item.cartItemId} className="flex items-center gap-4 p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
                  <img src={item.imageUrl} alt={item.productName} className="w-20 h-20 object-contain" />
                  <div className="flex-1">
                    <h3 className="font-bold text-gray-800 leading-tight">{item.productName}</h3>
                    <p className="text-sm text-blue-600 font-bold">${item.price}</p>
                    
                    <div className="flex items-center justify-between mt-3">
                      <div className="flex items-center border rounded-lg bg-gray-50 overflow-hidden">
                        <button onClick={() => updateQuantity(item.cartItemId, item.count - 1)} className="px-3 py-1 hover:bg-gray-200">-</button>
                        <span className="px-4 text-xs font-bold">{item.count}</span>
                        <button onClick={() => updateQuantity(item.cartItemId, item.count + 1)} className="px-3 py-1 hover:bg-gray-200">+</button>
                      </div>
                      <button onClick={() => removeItem(item.cartItemId)} className="text-xs text-red-400 hover:text-red-600 font-medium underline">Remove</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* 요약 및 결제 버튼 (1/3 영역) */}
            <div className="lg:col-span-1">
              <div className="p-6 bg-white rounded-2xl shadow-sm border border-gray-100 sticky top-24">
                <h2 className="text-xl font-black mb-4 uppercase tracking-tight">Summary</h2>
                <div className="space-y-3 mb-6">
                  <div className="flex justify-between text-gray-500">
                    <span>Subtotal</span>
                    <span>${totalPrice}</span>
                  </div>
                  <div className="flex justify-between text-gray-500">
                    <span>Shipping</span>
                    <span className="text-green-600 font-bold uppercase text-xs">Calculated at checkout</span>
                  </div>
                  <div className="border-t pt-3 flex justify-between font-black text-xl">
                    <span>Total</span>
                    <span className="text-red-600">${totalPrice}</span>
                  </div>
                </div>
                <button 
                  onClick={() => checkout(items.map(i => i.cartItemId))}
                  className="w-full bg-black text-white py-4 rounded-xl font-black text-lg hover:bg-gray-800 transition-all active:scale-[0.98] shadow-lg"
                >
                  CHECKOUT
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default CartPage;