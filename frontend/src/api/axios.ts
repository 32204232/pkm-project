import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../store/authStore';

// [★30년차 보안/유지보수★] localhost:8080 하드코딩 제거. 환경 변수(.env) 사용
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

const addRefreshSubscriber = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

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

axiosInstance.interceptors.response.use(
  (response) => {
    return response.data.data;
  },
  async (error: AxiosError<any>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

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
        const response = await axios.post(
          `${API_BASE_URL}/api/members/reissue`,
          {},
          { withCredentials: true } // 쿠키의 Refresh Token을 보냄
        );

        const newAccessToken = response.data.data.accessToken;
        
        useAuthStore.getState().setAccessToken(newAccessToken);
        onTokenRefreshed(newAccessToken);
        
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);

      } catch (reissueError) {
        // [★30년차 UX 개선★] 무식한 window.location.href 대신 React Router를 부드럽게 타게 하거나 스토어 초기화만 시켜주어 프론트에서 Redirect 처리하도록 유도
        console.error('세션이 만료되었습니다. 다시 로그인해주세요.');
        useAuthStore.getState().logout();
        window.location.replace('/login'); // history 스택을 오염시키지 않는 replace 사용
        return Promise.reject(reissueError);
      } finally {
        isRefreshing = false;
      }
    }

    const serverMessage = error.response?.data?.message;
    if (serverMessage) {
      return Promise.reject(new Error(serverMessage));
    }
    return Promise.reject(error);
  }
  
);

export default axiosInstance;