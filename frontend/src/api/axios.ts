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
        // 성공 시 데이터만 바로 반환 (응답 편의성)
        return response.data; 
    },
    async (error: AxiosError<BaseResponse>) => {
        const originalRequest = error.config;
        const status = error.response?.status;

        // 401 Unauthorized: 토큰 만료 시 처리
        if (status === 401) {
            console.error("인증이 만료되었습니다.");
            localStorage.removeItem('token');
            // 실무 팁: 여기서 Refresh Token이 있다면 재발급 로직을 호출합니다.
            // 지금은 가장 확실한 방법인 로그인 페이지로 유도합니다.
            window.location.href = '/login';
        }

        // 403 Forbidden: 권한 부족
        if (status === 403) {
            alert("접근 권한이 없습니다.");
        }

        // 백엔드에서 보낸 에러 메시지가 있다면 출력
        if (error.response?.data?.message) {
            console.error(`[API Error] ${error.response.data.message}`);
        }

        return Promise.reject(error);
    }
);

export default api;