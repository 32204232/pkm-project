import api from './axios';
import type { SignUpRequest, LoginRequest, TokenResponse } from '../types/auth';

const authApi = {
  // 1. 회원가입 요청
  signUp: async (data: SignUpRequest) => {
    // 백엔드의 MemberController @PostMapping("/signup")과 연결됨
    const response = await api.post<string>('/api/members/signup', data);
    return response.data;
  },

  // 2. 로그인 요청
  login: async (data: LoginRequest) => {
    // 백엔드의 MemberController @PostMapping("/login")과 연결됨
    // 성공하면 { accessToken: "ey..." } 형태의 데이터를 받습니다.
    const response = await api.post<TokenResponse>('/api/members/login', data);
    return response.data;
  }
};

export default authApi;