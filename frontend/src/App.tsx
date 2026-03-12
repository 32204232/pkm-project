import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import CartPage from './pages/CartPage';
import OrderSuccessPage from './pages/OrderSuccessPage';

// [★추가★] 문지기와 관리자 페이지 임포트
import AdminProtectedRoute from './components/auth/AdminProtectedRoute';
import AdminPage from './pages/AdminPage'; // (만들어두셨다는 관리자 페이지)

function App() {
  return (
    <BrowserRouter>
      <MainLayout>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/product/:id" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/order-success" element={<OrderSuccessPage />} />

          {/* [★추가★] 관리자 전용 구역 (문지기가 지키고 있음) */}
          <Route element={<AdminProtectedRoute />}>
            <Route path="/admin" element={<AdminPage />} />
          </Route>
        </Routes>
      </MainLayout>
    </BrowserRouter>
  );
}

export default App;