import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore'; //

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
          {/* 로고 영역 - 포켓볼 디자인 복구 */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-10 h-10 bg-red-500 rounded-full border-4 border-black relative overflow-hidden group-hover:rotate-12 transition-transform shadow-md">
                <div className="absolute top-1/2 w-full h-1 bg-black"></div>
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-3 h-3 bg-white border-2 border-black rounded-full"></div>
              </div>
              <span className="text-2xl font-black tracking-tighter italic">PKM STORE</span>
            </Link>
          </div>

          {/* 우측 아이콘 & 인증 영역 */}
          <div className="flex items-center space-x-5">
            <Link to="/cart" className="relative p-2 text-gray-600 hover:text-blue-600 font-bold text-sm">
              CART
            </Link>

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
                <Link to="/login" className="text-sm font-bold text-gray-700 hover:text-blue-600">Login</Link>
                <Link to="/signup" className="px-4 py-2 bg-black text-white text-sm font-bold rounded-lg hover:bg-gray-800">Sign Up</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

// [중요] 이 부분이 있어야 MainLayout에서 에러가 안 납니다!
export default Navbar;