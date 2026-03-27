import React, { useState } from 'react';
import { useProductList } from '../hooks/useProduct';
import { productApi } from '../api/productApi';

const AdminPage = () => {
  const { products, loading, refresh } = useProductList(); 
  
  // 1. 상태 관리: 백엔드 Enums 및 필수 필드 추가
  const [form, setForm] = useState({
    name: '',
    price: 0,
    stockQuantity: 10,
    category: 'BOOSTER_BOX',     // 기본값: 부스터 박스
    series: 'SCARLET_VIOLET',    // 기본값: 스칼렛&바이올렛
    status: 'ON_SALE',           // 기본값: 판매 중
    releaseDate: new Date().toISOString().split('T')[0] // 오늘 날짜 기본값
  });
  
  const [imageFile, setImageFile] = useState<File | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setImageFile(e.target.files[0]);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // productApi.createProduct는 내부적으로 FormData를 생성하여 전송합니다.
      await productApi.createProduct(form, imageFile); 
      
      alert('포켓몬 박스가 성공적으로 진열대에 올랐습니다! 삐까!');
      
      // 등록 성공 시 폼 초기화 (Enums 값 포함)
      setForm({
        name: '',
        price: 0,
        stockQuantity: 10,
        category: 'BOOSTER_BOX',
        series: 'SCARLET_VIOLET',
        status: 'ON_SALE',
        releaseDate: new Date().toISOString().split('T')[0]
      });
      setImageFile(null);
      const fileInput = document.getElementById('box-image') as HTMLInputElement;
      if (fileInput) fileInput.value = '';
      
      refresh(); 
    } catch (err: any) {
      console.error(err);
      alert('등록 실패: 데이터 규격이 맞지 않거나 서버에 문제가 생겼습니다.');
    }
  };

  if (loading) return <div className="p-10 text-center">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-10 p-6">
      
      {/* 상품 등록 폼 */}
      <section className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
        <h2 className="text-xl font-bold mb-4 text-blue-800">새 포켓몬 상자 입고</h2>
        <form onSubmit={handleRegister} className="grid grid-cols-2 gap-6">
          
          <div className="flex flex-col col-span-2">
            <label className="text-sm text-gray-600 mb-1">상자 이름</label>
            <input type="text" placeholder="예: 확장팩 스노해저드" className="border p-2 rounded focus:ring-2 focus:ring-blue-400" 
                   value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
          </div>

          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">가격 (원)</label>
            <input type="number" className="border p-2 rounded focus:ring-2 focus:ring-blue-400" 
                   value={form.price || ''} onChange={e => setForm({...form, price: Number(e.target.value)})} required />
          </div>

          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">입고 수량</label>
            <input type="number" className="border p-2 rounded focus:ring-2 focus:ring-blue-400" 
                   value={form.stockQuantity} onChange={e => setForm({...form, stockQuantity: Number(e.target.value)})} required />
          </div>

          {/* 카테고리 선택 */}
          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">카테고리</label>
            <select className="border p-2 rounded focus:ring-2 focus:ring-blue-400"
                    value={form.category} onChange={e => setForm({...form, category: e.target.value})}>
              <option value="BOOSTER_BOX">부스터 박스</option>
              <option value="STARTER_DECK">스타터 덱</option>
              <option value="SPECIAL_SET">스페셜 세트</option>
              <option value="SUPPLY">서플라이</option>
            </select>
          </div>

          {/* 시리즈 선택 */}
          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">시리즈</label>
            <select className="border p-2 rounded focus:ring-2 focus:ring-blue-400"
                    value={form.series} onChange={e => setForm({...form, series: e.target.value})}>
              <option value="SCARLET_VIOLET">스칼렛 & 바이올렛</option>
              <option value="SWORD_SHIELD">소드 & 실드</option>
              <option value="SUN_MOON">썬 & 문</option>
              <option value="CLASSIC">클래식</option>
            </select>
          </div>

          {/* 판매 상태 선택 */}
          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">판매 상태</label>
            <select className="border p-2 rounded focus:ring-2 focus:ring-blue-400"
                    value={form.status} onChange={e => setForm({...form, status: e.target.value})}>
              <option value="PRE_ORDER">예약 판매</option>
              <option value="ON_SALE">판매 중</option>
              <option value="OUT_OF_STOCK">품절</option>
              <option value="END_OF_SALE">판매 종료</option>
            </select>
          </div>

          <div className="flex flex-col">
            <label className="text-sm text-gray-600 mb-1">발매일</label>
            <input type="date" className="border p-2 rounded focus:ring-2 focus:ring-blue-400" 
                   value={form.releaseDate} onChange={e => setForm({...form, releaseDate: e.target.value})} required />
          </div>

          <div className="flex flex-col col-span-2">
            <label className="text-sm text-gray-600 mb-1">박스 이미지 (사진 첨부)</label>
            <input id="box-image" type="file" accept="image/*" className="border p-1 rounded bg-gray-50 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" 
                   onChange={handleFileChange} />
          </div>

          <button type="submit" className="col-span-2 mt-4 bg-blue-600 text-white py-3 rounded-lg font-bold hover:bg-blue-700 transition-colors shadow-md">
            진열대에 상품 올리기
          </button>
        </form>
      </section>

      {/* 현재 상품 목록 관리 */}
      <section className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
        <h2 className="text-xl font-bold mb-4">현재 재고 관리</h2>
        <div className="divide-y border-t">
          {products.map(p => (
            <div key={p.id} className="py-4 flex justify-between items-center hover:bg-gray-50 transition-colors px-2">
              <div className="flex items-center gap-4">
                <img src={p.imageUrl} alt={p.name} className="w-16 h-16 object-cover rounded" />
                <div>
                  <p className="font-bold text-gray-800">{p.name}</p>
                  <p className="text-xs text-blue-600 font-semibold">{p.series} | {p.category}</p>
                  <p className="text-sm text-gray-500">가격: {p.price.toLocaleString()}원</p>
                  <p className="text-sm text-gray-500">
                    재고: <span className={p.stockQuantity === 0 ? "text-red-500 font-bold" : "text-green-600 font-bold"}>{p.stockQuantity}개</span>
                  </p>
                </div>
              </div>
              <div className="flex gap-2">
                <button className="text-sm border border-gray-300 bg-white px-3 py-1 rounded hover:bg-gray-100">수정</button>
                <button className="text-sm border border-red-200 bg-red-50 text-red-600 px-3 py-1 rounded hover:bg-red-100"
                        onClick={async () => {
                          if (window.confirm('정말 삭제하시겠습니까?')) {
                            await productApi.deleteProduct(p.id);
                            refresh();
                          }
                        }}>삭제</button>
              </div>
            </div>
          ))}
          
          {products.length === 0 && (
            <p className="py-8 text-center text-gray-500">현재 등록된 포켓몬 상자가 없습니다.</p>
          )}
        </div>
      </section>

    </div>
  );
};

export default AdminPage;