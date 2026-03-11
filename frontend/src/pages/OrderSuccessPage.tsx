import React from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../components/layout/MainLayout';

const OrderSuccessPage = () => {
  return (
    <MainLayout>
      <div className="max-w-md mx-auto text-center py-20">
        <div className="w-24 h-24 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-8 text-5xl">
          ✓
        </div>
        <h1 className="text-4xl font-black mb-4 uppercase italic">Thank You!</h1>
        <p className="text-gray-500 mb-10 leading-relaxed">
          Your order has been placed successfully.<br />
          We will start shipping your Korean Pokémon boxes as soon as possible.
        </p>
        <div className="space-y-4">
          <Link to="/" className="block w-full bg-black text-white py-4 rounded-xl font-black hover:bg-gray-800 transition-all">
            CONTINUE SHOPPING
          </Link>
          <button className="block w-full border border-gray-200 py-4 rounded-xl font-bold text-gray-600 hover:bg-gray-50 transition-all">
            VIEW ORDER DETAILS
          </button>
        </div>
      </div>
    </MainLayout>
  );
};

export default OrderSuccessPage;