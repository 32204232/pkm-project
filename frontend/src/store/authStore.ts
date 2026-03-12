import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// 1. 금고에 담을 데이터의 설계도(Type)
interface AuthState {
  isLoggedIn: boolean;
  accessToken: string | null;
  userEmail: string | null;
  userRole: string | null; // [★추가★] 관리자 여부 확인용 권한 (USER / ADMIN)
  
  // 액션(함수)들 - login 함수가 role도 받도록 수정!
  login: (token: string, email: string, role: string) => void; 
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
      userRole: null, // [★추가★]

      // 로그인 처리: 데이터 채우고 토큰은 localStorage에 자동 저장됨
      login: (token, email, role) => { // [★수정★] role 매개변수 추가
        localStorage.setItem('accessToken', token); 
        set({
          isLoggedIn: true,
          accessToken: token,
          userEmail: email,
          userRole: role, // [★추가★] 스토어에 권한 저장
        });
      },

      // 로그아웃 처리: 데이터 비우기
      logout: () => {
        localStorage.removeItem('accessToken');
        set({
          isLoggedIn: false,
          accessToken: null,
          userEmail: null,
          userRole: null, // [★추가★]
        });
      },
    }),
    {
      name: 'auth-storage', // localStorage에 저장될 키 이름
    }
  )
);