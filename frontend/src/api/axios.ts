// 위치: frontend/src/api/axios.ts

import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import useAuthStore from './authStore';

// 1. 기본 설정
export const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

// [★실무 핵심★] 재발급 상태를 관리하기 위한 변수들
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

const addRefreshSubscriber = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

// 2. 요청 인터셉터: 모든 요청에 토큰 자동 부착
axiosInstance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. 응답 인터셉터: 데이터 언래핑 + 심폐소생술(토큰 재발급)
axiosInstance.interceptors.response.use(
  (response) => {
    /**
     * [★30년 차 노하우★] 데이터 언래핑
     * 백엔드의 ApiResponse<T> 구조 { success, status, message, data } 에서
     * 실제 필요한 data 부분만 반환합니다. 
     * 이렇게 하면 API 호출부에서 .data.data를 쓸 필요가 없습니다.
     */
    return response.data.data;
  },
  async (error: AxiosError<any>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // 401 에러(토큰 만료) 발생 시
    if (error.response?.status === 401 && !originalRequest._retry) {
      
      if (isRefreshing) {
        return new Promise((resolve) => {
          addRefreshSubscriber((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(axiosInstance(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        console.log('토큰 만료 감지: 새 토큰을 요청합니다...');
        // 주의: 재발급 요청은 무한 루프 방지를 위해 axiosInstance가 아닌 원본 axios를 사용하거나 별도 설정 필요
        const response = await axios.post(
          'http://localhost:8080/api/members/reissue',
          {},
          { withCredentials: true }
        );

        const newAccessToken = response.data.data.accessToken;
        
        // 스토어 업데이트 및 대기 중인 요청들 해제
        useAuthStore.getState().setAccessToken(newAccessToken);
        onTokenRefreshed(newAccessToken);
        
        // 원래 요청 다시 시도
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);

      } catch (reissueError) {
        console.error('세션이 만료되었습니다. 다시 로그인해주세요.');
        useAuthStore.getState().logout();
        window.location.href = '/login';
        return Promise.reject(reissueError);
      } finally {
        isRefreshing = false;
      }
    }

    /**
     * [★추가된 에러 처리★]
     * 서버에서 내려준 ApiResponse의 에러 메시지가 있다면 그걸 사용합니다.
     */
    const serverMessage = error.response?.data?.message;
    if (serverMessage) {
      return Promise.reject(new Error(serverMessage));
    }

    return Promise.reject(error);
  }
);