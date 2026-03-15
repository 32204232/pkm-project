// src/api/authApi.ts
import api from './axios';
import type { SignUpRequest, LoginRequest, TokenResponse } from '../types/auth';

const authApi = {
  // 1. 회원가입 요청
  signUp: (data: SignUpRequest): Promise<string> => {
    // 이제 인터셉터가 알아서 data만 주므로 바로 리턴합니다.
    return api.post('/api/members/signup', data);
  },

  // 2. 로그인 요청
  login: (data: LoginRequest): Promise<TokenResponse> => {
    // 마찬가지로 response.data를 또 꺼내지 않고 그대로 리턴합니다.
    return api.post('/api/members/login', data);
  }
};

export default authApi;