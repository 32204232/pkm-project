import { useCartList } from '../hooks/useCartList';
import { useOrder } from '../hooks/useOrder';
import MainLayout from '../components/layout/MainLayout';
import CartSummary from '../components/cart/CartSummary';

const CartPage = () => {
  const { items, loading, updateQuantity, removeItem, totalPrice } = useCartList();
  const { checkout, isOrdering } = useOrder();

  // items가 존재할 때만 상품명 생성 로직 실행
  const orderName = items && items.length > 0 
    ? (items.length > 1 ? `${items[0].productName} 외 ${items.length - 1}건` : items[0].productName)
    : "주문 상품";

  if (loading) return <MainLayout><div className="p-20 text-center">Loading...</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-black mb-8 italic uppercase">Shopping Cart</h1>
        {(!items || items.length === 0) ? (
          <div className="py-20 text-center bg-gray-50 rounded-2xl border-2 border-dashed">
             장바구니가 비어 있습니다!
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
               {/* CartItem 렌더링 로직... */}
            </div>
            <div className="lg:col-span-1">
              <CartSummary 
                totalPrice={totalPrice} 
                onCheckout={() => checkout(orderName)} 
                isLoading={isOrdering}
              />
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default CartPage;