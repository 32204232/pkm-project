import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';

export default function AdminProtectedRoute() {
  const { isLoggedIn, userRole } = useAuthStore();

  // 로그인을 안 했거나, 권한이 ADMIN이 아니면 쫓아낸다!
  if (!isLoggedIn || userRole !== 'ADMIN') {
    alert('접근 권한이 없습니다! 포켓몬 센터로 돌아갑니다. 삐까!');
    return <Navigate to="/" replace />; // 메인 페이지로 강제 이동
  }

  // 관리자가 맞다면 정상적으로 하위 라우트(관리자 페이지)를 보여준다.
  return <Outlet />; 
}