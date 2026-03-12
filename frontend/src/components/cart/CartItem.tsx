import type { CartResponse } from '../../types/cart';

interface Props {
  item: CartResponse;
  onUpdate: (id: number, count: number) => void;
  onRemove: (id: number) => void;
}

const CartItem = ({ item, onUpdate, onRemove }: Props) => {
  return (
    <div className="flex items-center gap-4 p-4 bg-white rounded-2xl shadow-sm border border-gray-100">
      <img src={item.imageUrl} alt={item.productName} className="w-20 h-20 object-contain" />
      <div className="flex-1">
        <h3 className="font-bold text-gray-800 leading-tight">{item.productName}</h3>
        <p className="text-sm text-blue-600 font-bold">${item.price.toLocaleString()}</p>
        
        <div className="flex items-center justify-between mt-3">
          <div className="flex items-center border rounded-lg bg-gray-50 overflow-hidden">
            <button 
              onClick={() => onUpdate(item.cartItemId, item.count - 1)} 
              className="px-3 py-1 hover:bg-gray-200"
            >-</button>
            <span className="px-4 text-xs font-bold">{item.count}</span>
            <button 
              onClick={() => onUpdate(item.cartItemId, item.count + 1)} 
              className="px-3 py-1 hover:bg-gray-200"
            >+</button>
          </div>
          <button 
            onClick={() => onRemove(item.cartItemId)} 
            className="text-xs text-red-400 hover:text-red-600 font-medium underline"
          >Remove</button>
        </div>
      </div>
    </div>
  );
};

export default CartItem;