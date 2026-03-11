import axios from 'axios';

// 1. 공통 설정 (백엔드 주소 고정)
const api = axios.create({
  baseURL: 'http://localhost:8080', // 우리가 띄워둔 Spring Boot 서버 주소
  headers: {
    'Content-Type': 'application/json',
  },
});

// 2. 요청 인터셉터 (Request Interceptor) - 핵심 중의 핵심!
// 프론트엔드가 백엔드 API를 찌르기 직전에 무조건 여기를 거쳐갑니다.
api.interceptors.request.use(
  (config) => {
    // 브라우저 금고(로컬 스토리지)에서 VIP 팔찌(JWT 토큰)를 꺼냅니다.
    const token = localStorage.getItem('accessToken');
    
    // 토큰이 존재하면, 백엔드 문지기가 요구했던 "Bearer " 양식에 맞춰서 헤더에 딱 붙여줍니다.
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// TODO: 나중에 토큰이 만료되었을 때(401 에러) 자동으로 로그아웃 시키는 응답 인터셉터도 여기에 추가할 수 있습니다.

export default api;