// 회원가입할 때 백엔드로 보낼 상자
export interface SignUpRequest {
  email: string;
  password: string;
  nickname: string;
}

// 로그인할 때 백엔드로 보낼 상자
export interface LoginRequest {
  email: string;
  password: string;
}

// 로그인 성공 시 백엔드에서 받을 상자
export interface TokenResponse {
  accessToken: string;
  // refreshToken: string; // (리프레시 토큰이 있다면 냅두고)
  role: string; // [★추가★] 백엔드에서 권한도 같이 내려줄 거다!
}