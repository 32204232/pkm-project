import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

const Navbar = () => {
  const { isLoggedIn, logout, userEmail } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    alert('Logged out successfully.');
    navigate('/');
  };

  return (
    <nav className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-20">
          {/* 로고 영역 */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-10 h-10 bg-red-500 rounded-full border-4 border-black relative overflow-hidden group-hover:rotate-12 transition-transform shadow-md">
                <div className="absolute top-1/2 w-full h-1 bg-black"></div>
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-3 h-3 bg-white border-2 border-black rounded-full"></div>
              </div>
              <span className="text-2xl font-black tracking-tighter italic">PKM STORE</span>
            </Link>
          </div>

          {/* 중앙 메뉴 (필요시 추가) */}
          <div className="hidden md:flex items-center space-x-8 text-sm font-bold uppercase tracking-widest text-gray-500">
            <Link to="/" className="hover:text-blue-600 transition-colors">Booster Packs</Link>
            <Link to="#" className="hover:text-black">Special Sets</Link>
            <Link to="#" className="hover:text-black">About Shipping</Link>
          </div>

          {/* 우측 아이콘 & 인증 영역 */}
          <div className="flex items-center space-x-5">
            {/* 장바구니 아이콘 (뱃지 포함) */}
            <Link to="/cart" className="relative p-2 text-gray-600 hover:text-blue-600">
              <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 11V7a4 4 0 11-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-red-100 transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full">0</span>
            </Link>

            {/* 로그인 상태에 따른 버튼 */}
            <div className="h-6 w-[1px] bg-gray-200 mx-2"></div>
            
            {isLoggedIn ? (
              <div className="flex items-center gap-4">
                <span className="hidden lg:block text-xs font-medium text-gray-400">{userEmail}</span>
                <button 
                  onClick={handleLogout}
                  className="text-sm font-bold text-gray-700 hover:text-red-500 transition-colors"
                >
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-4">
                <Link to="/login" className="text-sm font-bold text-gray-700 hover:text-blue-600 transition-colors">Login</Link>
                <Link to="/signup" className="px-4 py-2 bg-black text-white text-sm font-bold rounded-lg hover:bg-gray-800 transition-colors">Sign Up</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;