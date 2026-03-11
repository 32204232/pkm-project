import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import authApi from '../api/authApi';
import { useAuthStore } from '../store/authStore';

const LoginPage = () => {
  // 1. 입력 상태 관리
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // 2. 도구 준비
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  // 3. 로그인 제출 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      // API 호출
      const response = await authApi.login({ email, password });
      
      // 금고(Zustand)에 저장
      login(response.accessToken, email);
      
      // 메인 페이지로 이동
      alert('반가워요! 로그인에 성공했습니다.');
      navigate('/'); 
    } catch (err: any) {
      setError('이메일 또는 비밀번호를 확인해주세요.');
      console.error(err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="max-w-md w-full space-y-8 p-10 bg-white rounded-2xl shadow-xl">
        {/* 헤더 부분 */}
        <div className="text-center">
          <h2 className="text-3xl font-extrabold text-blue-600">PKM STORE</h2>
          <p className="mt-2 text-sm text-gray-600">포켓몬 상자를 만나보세요!</p>
        </div>

        {/* 로그인 폼 */}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">이메일</label>
              <input
                type="email"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="pokemon@store.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">비밀번호</label>
              <input
                type="password"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          {/* 에러 메시지 */}
          {error && <p className="text-red-500 text-xs italic">{error}</p>}

          <div>
            <button
              type="submit"
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              로그인하기
            </button>
          </div>
        </form>

        {/* 회원가입 유도 */}
        <div className="text-center">
          <p className="text-sm text-gray-600">
            계정이 없으신가요?{' '}
            <Link to="/signup" className="font-medium text-blue-600 hover:text-blue-500">
              회원가입 하러가기
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;