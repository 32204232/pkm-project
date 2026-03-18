import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

// 유저 정보 타입 정의
interface User {
  email: string;
  nickname: string;
  role: 'USER' | 'ADMIN';
}

interface AuthState {
  accessToken: string | null;
  user: User | null;
  isLoggedIn: boolean;
  
  // Actions
  setAccessToken: (token: string | null) => void;
  login: (accessToken: string, user: User) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null, // Access Token은 보안을 위해 세션/로컬 스토리지에 직접 저장하지 않음 (메모리 보관)
      user: null,
      isLoggedIn: false,

      // 토큰만 업데이트 (재발급 시 사용)
      setAccessToken: (token) => 
        set({ accessToken: token, isLoggedIn: !!token }),

      // 로그인 성공 시 호출
      login: (accessToken, user) => 
        set({ 
          accessToken, 
          user, 
          isLoggedIn: true 
        }),

      // 로그아웃 시 초기화
      logout: () => {
        // 백엔드의 Refresh Token 쿠키는 axios 요청이나 로그아웃 API에서 별도로 처리
        set({ 
          accessToken: null, 
          user: null, 
          isLoggedIn: false 
        });
      },
    }),
    {
      name: 'pkm-auth-storage', // 로컬 스토리지에 저장될 키 이름
      storage: createJSONStorage(() => localStorage),
      // [★중요★] accessToken은 스토리지에 저장하지 않고 유저 정보만 유지하도록 설정
      partialize: (state) => ({ 
        user: state.user, 
        isLoggedIn: state.isLoggedIn 
      }),
    }
  )
);

export default useAuthStore;