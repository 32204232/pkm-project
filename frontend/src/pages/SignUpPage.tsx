import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import authApi from '../api/authApi';

const SignUpPage = () => {
  // 1. 상태 관리 (이메일, 비밀번호, 비번확인, 닉네임)
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    nickname: '',
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // 2. 입력 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 3. 가입 제출
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // 유효성 검사: 비밀번호 일치 확인
    if (formData.password !== formData.confirmPassword) {
      setError('비밀번호가 서로 일치하지 않습니다.');
      return;
    }

    try {
      await authApi.signUp({
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
      });
      
      alert('가입을 축하합니다! 이제 로그인을 해주세요.');
      navigate('/login'); // 가입 성공 시 로그인 페이지로!
    } catch (err: any) {
      setError(err.response?.data || '회원가입 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="max-w-md w-full space-y-8 p-10 bg-white rounded-2xl shadow-xl">
        <div className="text-center">
          <h2 className="text-3xl font-extrabold text-blue-600">JOIN US</h2>
          <p className="mt-2 text-sm text-gray-600">포켓몬 스토어의 회원이 되어보세요!</p>
        </div>

        <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
          <div className="space-y-4">
            {/* 이메일 */}
            <div>
              <label className="text-sm font-medium text-gray-700">이메일 주소</label>
              <input
                name="email"
                type="email"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="example@pkm.com"
                onChange={handleChange}
              />
            </div>
            {/* 닉네임 */}
            <div>
              <label className="text-sm font-medium text-gray-700">닉네임</label>
              <input
                name="nickname"
                type="text"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="지우"
                onChange={handleChange}
              />
            </div>
            {/* 비밀번호 */}
            <div>
              <label className="text-sm font-medium text-gray-700">비밀번호</label>
              <input
                name="password"
                type="password"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="••••••••"
                onChange={handleChange}
              />
            </div>
            {/* 비밀번호 확인 */}
            <div>
              <label className="text-sm font-medium text-gray-700">비밀번호 확인</label>
              <input
                name="confirmPassword"
                type="password"
                required
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="••••••••"
                onChange={handleChange}
              />
            </div>
          </div>

          {error && <p className="text-red-500 text-xs italic">{error}</p>}

          <button
            type="submit"
            className="w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-blue-600 hover:bg-blue-700 transition-colors"
          >
            가입하기
          </button>
        </form>

        <div className="text-center text-sm">
          <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
            이미 계정이 있으신가요? 로그인하기
          </Link>
        </div>
      </div>
    </div>
  );
};

export default SignUpPage;