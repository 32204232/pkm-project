// src/api/authApi.ts
import { axiosInstance as api } from './axios';
import type { SignUpRequest, LoginRequest, TokenResponse } from '../types/auth';

/**
 * 30년 차 개발자 Tip: 
 * API 객체는 도메인별로 명확히 분리하고, 
 * 함수 명은 행위(verb) 위주로 간결하게 작성합니다.
 */
const authApi = {
  // 1. 회원가입: 성공 시 생성된 회원 ID(Long)나 메시지를 받음
  signUp: (data: SignUpRequest): Promise<any> => {
    return api.post('/api/members/signup', data);
  },

  // 2. 로그인: 성공 시 AccessToken이 담긴 TokenResponse를 받음
  login: (data: LoginRequest): Promise<TokenResponse> => {
    return api.post('/api/members/login', data);
  },

  // 3. 로그아웃: 세션 종료 및 서버 측 리프레시 토큰 무효화
  logout: (): Promise<void> => {
    return api.post('/api/members/logout');
  }
};

export default authApi;