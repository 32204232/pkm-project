
interface Props {
  quantity: number;
  setQuantity: (q: number) => void;
  isOutOfStock: boolean;
  onAddToCart: () => void;
  cartLoading: boolean;
}

const ProductActions = ({ quantity, setQuantity, isOutOfStock, onAddToCart, cartLoading }: Props) => (
  <div className="space-y-6 border-t border-b border-gray-100 py-6">
    {/* 배송 정보 요약 */}
    <div className="space-y-2 text-sm text-gray-600">
      <div className="flex"><span className="w-32 font-bold">Condition</span><span className="text-green-600 font-bold">New / Sealed</span></div>
      <div className="flex"><span className="w-32 font-bold">Shipping</span><span>Worldwide from Korea</span></div>
    </div>

    {/* 수량 조절 */}
    <div className="flex items-center">
      <span className="w-32 font-bold text-sm text-gray-600">Quantity</span>
      <div className={`flex items-center border rounded-lg overflow-hidden ${isOutOfStock ? 'opacity-50' : 'bg-white'}`}>
        <button onClick={() => setQuantity(Math.max(1, quantity - 1))} disabled={isOutOfStock} className="px-4 py-2 hover:bg-gray-100 border-r">-</button>
        <span className="px-6 py-2 font-bold min-w-[50px] text-center">{quantity}</span>
        <button onClick={() => setQuantity(quantity + 1)} disabled={isOutOfStock} className="px-4 py-2 hover:bg-gray-100 border-l">+</button>
      </div>
    </div>

    {/* 구매 버튼 */}
    <div className="flex gap-4">
      <button 
        onClick={onAddToCart}
        disabled={cartLoading || isOutOfStock}
        className={`flex-1 py-4 rounded-xl font-black text-lg shadow-lg transition-all ${
          isOutOfStock ? 'bg-gray-300 text-gray-500 cursor-not-allowed' : 'bg-black text-white hover:bg-gray-800 active:scale-[0.98]'
        }`}
      >
        {isOutOfStock ? 'OUT OF STOCK' : cartLoading ? 'ADDING...' : 'ADD TO CART'}
      </button>
    </div>
  </div>
);

export default ProductActions;