import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// 1. 금고에 담을 데이터의 설계도(Type)
interface AuthState {
  isLoggedIn: boolean;
  accessToken: string | null;
  userEmail: string | null;
  userRole: string | null;
  
  // 액션(함수)들
  login: (token: string, email: string, role: string) => void; 
  logout: () => void;
  setAccessToken: (token: string) => void; // [★추가★] 토큰만 갱신하는 함수 타입 정의
}

// 2. 진짜 금고(Store) 생성
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      // 초기 상태
      isLoggedIn: false,
      accessToken: null,
      userEmail: null,
      userRole: null,

      // 로그인 처리
      login: (token, email, role) => { 
        localStorage.setItem('accessToken', token); 
        set({
          isLoggedIn: true,
          accessToken: token,
          userEmail: email,
          userRole: role,
        });
      },

      // 로그아웃 처리
      logout: () => {
        localStorage.removeItem('accessToken');
        set({
          isLoggedIn: false,
          accessToken: null,
          userEmail: null,
          userRole: null,
        });
      },

      // [★추가★] AccessToken 재발급 시 토큰만 조용히 갱신하는 함수
      setAccessToken: (token) => {
        localStorage.setItem('accessToken', token);
        set({ accessToken: token });
      },
    }),
    {
      name: 'auth-storage', // localStorage에 저장될 키 이름
    }
  )
);