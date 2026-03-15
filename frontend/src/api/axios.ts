import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '../store/authStore'; // [★핵심 1] Zustand 금고 소환

export interface BaseResponse<T = any> {
    success: boolean;
    status: number;
    message: string;
    data: T;
}

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
    timeout: 10000,
});

// [Request Interceptor] 모든 요청에 자동으로 토큰 주입
api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // [★핵심 2] localStorage가 아니라 Zustand 상태에서 직접 토큰을 꺼냅니다!
        const token = useAuthStore.getState().accessToken;
        
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// [Response Interceptor] 전역 에러 처리 및 데이터 언래핑
api.interceptors.response.use(
    (response) => {
        return response.data.data; 
    },
    async (error: AxiosError<BaseResponse>) => {
        const status = error.response?.status;

        // 401이나 403 에러 시 로그아웃 처리
        if (status === 401 || status === 403) {
            alert('인증이 만료되었거나 권한이 없습니다. 다시 로그인해주세요.');
            useAuthStore.getState().logout(); // Zustand 로그아웃 호출
            window.location.href = '/login';
        }
        
        return Promise.reject(error);
    }
);

export default api;