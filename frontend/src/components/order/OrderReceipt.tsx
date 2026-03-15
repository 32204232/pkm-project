import type { OrderResponse } from '../../api/orderApi';

const OrderReceipt = ({ data }: { data: OrderResponse }) => {
  return (
    <div className="max-w-md mx-auto bg-white p-8 rounded-2xl shadow-sm border border-gray-100 mt-8 text-left">
      <h2 className="text-xl font-bold border-b pb-4 mb-4">Order Receipt</h2>
      
      <div className="space-y-4 mb-6">
        {data.orderItems.map((item, idx) => (
          <div key={idx} className="flex items-center gap-4">
            <img src={item.imageUrl} alt={item.productName} className="w-12 h-12 object-contain" />
            <div className="flex-1">
              <p className="font-bold text-sm text-gray-800">{item.productName}</p>
              <p className="text-xs text-gray-500">Qty: {item.count}</p>
            </div>
            <p className="font-bold text-gray-800">${(item.orderPrice * item.count).toLocaleString()}</p>
          </div>
        ))}
      </div>
      
      <div className="border-t pt-4 flex justify-between items-center font-black text-xl">
        <span>Total Paid</span>
        <span className="text-red-600">${data.totalPrice.toLocaleString()}</span>
      </div>
    </div>
  );
};

export default OrderReceipt;