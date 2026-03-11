import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout'; // [추가] MainLayout 임포트
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import CartPage from './pages/CartPage';
import OrderSuccessPage from './pages/OrderSuccessPage';

function App() {
  return (
    <BrowserRouter>
      {/* MainLayout으로 Routes를 감싸줍니다. 
        이렇게 하면 모든 페이지 상단에 Navbar가 고정됩니다. 
      */}
      <MainLayout>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/product/:id" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/order-success" element={<OrderSuccessPage />} />
        </Routes>
      </MainLayout>
    </BrowserRouter>
  );
}

export default App;