// 위치: frontend/src/api/axios.ts

import axios from 'axios';
import useAuthStore from '../store/authStore'; // Zustand 상태 관리 (토큰 저장소)

// 1. 기본 Axios 인스턴스 생성
export const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // 백엔드 주소 (운영 서버 배포 시 변경 필요)
  withCredentials: true, // [★가장 중요★] 백엔드에서 만든 HttpOnly 쿠키(Refresh Token)를 주고받으려면 무조건 true여야 합니다!
});

// 2. 요청(Request) 인터셉터: 백엔드로 API를 쏠 때마다 자동으로 Access Token을 헤더에 붙여줍니다.
axiosInstance.interceptors.request.use(
  (config) => {
    // Zustand 스토어에서 현재 들고 있는 Access Token을 꺼냅니다.
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. 응답(Response) 인터셉터: 401 에러(토큰 만료)가 터졌을 때 심폐소생술을 시도합니다.
axiosInstance.interceptors.response.use(
  (response) => response, // 성공하면 그대로 통과
  async (error) => {
    // 에러가 났던 원래의 요청 정보 (결제 버튼 누르기 등)
    const originalRequest = error.config;

    // 만약 에러가 401(권한 없음/토큰 만료)이고, 아직 재시도를 안 해봤다면?
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // 무한 루프 방지용 꼬리표 달기

      try {
        // [★심폐소생술★] 백엔드의 /reissue API를 찔러서 새 토큰을 달라고 조릅니다.
        // 이때 withCredentials: true 덕분에 브라우저가 알아서 쿠키(Refresh Token)를 백엔드로 보냅니다.
        const response = await axios.post(
          'http://localhost:8080/api/members/reissue',
          {}, 
          { withCredentials: true }
        );

        // 백엔드가 던져준 새로운 Access Token을 낚아챕니다.
        const newAccessToken = response.data.data.accessToken;

        // Zustand 스토어(전역 상태)에 새 토큰을 업데이트합니다.
        useAuthStore.getState().setAccessToken(newAccessToken);

        // 아까 실패했던 원래 요청의 헤더에 새 토큰을 갈아 끼우고 다시 발사합니다! (고객은 에러가 났었는지도 모릅니다)
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);

      } catch (reissueError) {
        // Refresh Token마저 만료되었거나 조작되어 심폐소생술에 실패한 경우
        console.error('토큰 재발급 실패. 다시 로그인해야 합니다');
        
        // 스토어를 비우고 로그인 페이지로 강제 추방합니다.
        useAuthStore.getState().logout();
        window.location.href = '/login';
        
        return Promise.reject(reissueError);
      }
    }

    // 401 에러가 아니거나, 재시도했는데도 실패한 경우엔 원래 에러를 뱉어냅니다.
    return Promise.reject(error);
  }
);