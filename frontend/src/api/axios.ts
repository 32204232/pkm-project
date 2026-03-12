import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';

// 백엔드 ApiResponse 포맷과 일치시킴
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
        const token = localStorage.getItem('token');
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
        // 백엔드 ApiResponse { success, status, message, data } 중 data만 반환
        return response.data.data; 
    },
    async (error: AxiosError<BaseResponse>) => {
        // 에러 처리는 기존대로 유지 (401, 403 등)
        return Promise.reject(error);
    }
);

export default api;