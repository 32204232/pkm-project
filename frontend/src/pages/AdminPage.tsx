import React, { useState } from 'react';
import { useProductList } from '../hooks/useProduct';
import { productApi } from '../api/productApi';
import MainLayout from '../components/layout/MainLayout';

const AdminPage = () => {
  const { products, loading, refresh } = useProductList(); // 목록 가져오기
  const [form, setForm] = useState({ name: '', price: 0, stockQuantity: 10, imageUrl: '' });

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await productApi.registerProduct(form);
      alert('상품이 성공적으로 등록되었습니다!');
      refresh(); // 목록 새로고침
    } catch (err) {
      alert('등록 실패: 권한을 확인하세요.');
    }
  };

  if (loading) return <MainLayout><div>Loading...</div></MainLayout>;

  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto space-y-10">
        {/* 상품 등록 폼 */}
        <section className="bg-white p-6 rounded-2xl shadow-sm border">
          <h2 className="text-xl font-bold mb-4">Register New Box</h2>
          <form onSubmit={handleRegister} className="grid grid-cols-2 gap-4">
            <input type="text" placeholder="Box Name" className="border p-2 rounded" onChange={e => setForm({...form, name: e.target.value})} />
            <input type="number" placeholder="Price (USD)" className="border p-2 rounded" onChange={e => setForm({...form, price: Number(e.target.value)})} />
            <input type="number" placeholder="Stock" className="border p-2 rounded" onChange={e => setForm({...form, stockQuantity: Number(e.target.value)})} />
            <input type="text" placeholder="Image URL" className="border p-2 rounded" onChange={e => setForm({...form, imageUrl: e.target.value})} />
            <button className="col-span-2 bg-blue-600 text-white py-2 rounded font-bold">Register Box</button>
          </form>
        </section>

        {/* 현재 상품 목록 관리 */}
        <section className="bg-white p-6 rounded-2xl shadow-sm border">
          <h2 className="text-xl font-bold mb-4">Inventory Management</h2>
          <div className="divide-y">
            {products.map(p => (
              <div key={p.id} className="py-4 flex justify-between items-center">
                <div className="flex items-center gap-4">
                  <img src={p.imageUrl} alt="" className="w-12 h-12 object-contain" />
                  <div>
                    <p className="font-bold">{p.name}</p>
                    <p className="text-sm text-gray-500">Stock: <span className={p.stockQuantity === 0 ? "text-red-500 font-bold" : ""}>{p.stockQuantity}</span></p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <button className="text-sm border px-3 py-1 rounded">Edit</button>
                  <button className="text-sm border px-3 py-1 rounded text-red-500">Delete</button>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </MainLayout>
  );
};

export default AdminPage;