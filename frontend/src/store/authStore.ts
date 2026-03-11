import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// 1. 금고에 담을 데이터의 설계도(Type)
interface AuthState {
  isLoggedIn: boolean;
  accessToken: string | null;
  userEmail: string | null;
  // 액션(함수)들
  login: (token: string, email: string) => void;
  logout: () => void;
}

// 2. 진짜 금고(Store) 생성
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      // 초기 상태
      isLoggedIn: false,
      accessToken: null,
      userEmail: null,

      // 로그인 처리: 데이터 채우고 토큰은 localStorage에 자동 저장됨
      login: (token, email) => {
        localStorage.setItem('accessToken', token); // Axios 인터셉터가 여기서 꺼내 씁니다.
        set({
          isLoggedIn: true,
          accessToken: token,
          userEmail: email,
        });
      },

      // 로그아웃 처리: 데이터 비우기
      logout: () => {
        localStorage.removeItem('accessToken');
        set({
          isLoggedIn: false,
          accessToken: null,
          userEmail: null,
        });
      },
    }),
    {
      name: 'auth-storage', // localStorage에 저장될 키 이름
    }
  )
);