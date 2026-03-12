// src/components/cart/CartSummary.tsx

interface Props {
  totalPrice: number;
  onCheckout: () => void;
  isLoading: boolean; // [★추가] 이 줄이 없어서 에러가 났던 겁니다!
}

const CartSummary = ({ totalPrice, onCheckout, isLoading }: Props) => {
  return (
    <div className="p-6 bg-white rounded-2xl shadow-sm border border-gray-100 sticky top-24">
      <h2 className="text-xl font-black mb-4 uppercase tracking-tight">Summary</h2>
      <div className="space-y-3 mb-6">
        <div className="flex justify-between text-gray-500">
          <span>Subtotal</span>
          <span>${totalPrice.toLocaleString()}</span>
        </div>
        <div className="border-t pt-3 flex justify-between font-black text-xl">
          <span>Total</span>
          <span className="text-red-600">${totalPrice.toLocaleString()}</span>
        </div>
      </div>
      
      <button 
        onClick={onCheckout}
        disabled={isLoading} // [★추가] 로딩 중에는 버튼을 비활성화합니다.
        className={`w-full py-4 rounded-xl font-black text-lg transition-all active:scale-[0.98] shadow-lg
          ${isLoading 
            ? 'bg-gray-400 cursor-not-allowed' 
            : 'bg-black text-white hover:bg-gray-800'
          }`}
      >
        {isLoading ? 'PROCESSING...' : 'CHECKOUT'} {/* [★추가] 상태에 따라 텍스트 변경 */}
      </button>
    </div>
  );
};

export default CartSummary;