import React, { useState } from 'react';
import { useProductList } from '../hooks/useProduct';
import { productApi } from '../api/productApi';
// MainLayout은 주석 처리되어 있거나 삭제된 것 같지만, 혹시 있다면 유지!
// import MainLayout from '../components/layout/MainLayout';

const AdminPage = () => {
  const { products, loading, refresh } = useProductList(); // 목록 가져오기
  
  // 1. 상태 관리: 텍스트 정보와 파일(File 객체)을 따로 관리한다.
  const [form, setForm] = useState({ name: '', price: 0, stockQuantity: 10 });
  const [imageFile, setImageFile] = useState<File | null>(null); // [★추가★] 사진 파일 상태

  // 2. 파일 선택 시 처리 함수
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setImageFile(e.target.files[0]); // 선택된 첫 번째 파일을 저장
    }
  };

  // 3. 상품 등록(제출) 핸들러
  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // 우리가 백엔드에 맞춰 FormData로 만든 API 함수 호출!
      // (만약 productApi.ts에 함수 이름을 다른 걸로 해뒀다면 거기에 맞추거라)
      await productApi.createProduct(form, imageFile); 
      
      alert('포켓몬 박스가 성공적으로 진열대에 올랐습니다! 삐까!');
      
      // 등록 성공 시 폼 초기화 및 목록 새로고침
      setForm({ name: '', price: 0, stockQuantity: 10 });
      setImageFile(null);
      // 파일 input 태그의 값도 초기화해야 한다. (ref를 쓰면 더 좋지만 일단 생략)
      const fileInput = document.getElementById('box-image') as HTMLInputElement;
      if (fileInput) fileInput.value = '';
      
      refresh(); 
    } catch (err: any) {
      console.error(err);
      alert('등록 실패: 권한이 없거나 서버에 문제가 생겼습니다.');
    }
  };

  if (loading) return <div>Loading...</div>; // MainLayout이 있다면 감싸기

  return (
    // <MainLayout>
      <div className="max-w-4xl mx-auto space-y-10 p-6">
        
        {/* 상품 등록 폼 */}
        <section className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
          <h2 className="text-xl font-bold mb-4 text-blue-800">새 포켓몬 상자 입고</h2>
          <form onSubmit={handleRegister} className="grid grid-cols-2 gap-4">
            
            <div className="flex flex-col">
              <label className="text-sm text-gray-600 mb-1">상자 이름</label>
              <input type="text" placeholder="예: 스노해저드" className="border p-2 rounded focus:ring-2 focus:ring-blue-400 focus:outline-none" 
                     value={form.name} onChange={e => setForm({...form, name: e.target.value})} required />
            </div>

            <div className="flex flex-col">
              <label className="text-sm text-gray-600 mb-1">가격 (원)</label>
              <input type="number" placeholder="예: 30000" className="border p-2 rounded focus:ring-2 focus:ring-blue-400 focus:outline-none" 
                     value={form.price === 0 ? '' : form.price} onChange={e => setForm({...form, price: Number(e.target.value)})} required />
            </div>

            <div className="flex flex-col">
              <label className="text-sm text-gray-600 mb-1">입고 수량</label>
              <input type="number" placeholder="예: 100" className="border p-2 rounded focus:ring-2 focus:ring-blue-400 focus:outline-none" 
                     value={form.stockQuantity} onChange={e => setForm({...form, stockQuantity: Number(e.target.value)})} required />
            </div>

            {/* [★핵심 수정 부분★] 텍스트 URL 대신 파일 선택 input으로 변경 */}
            <div className="flex flex-col">
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
                  {/* [주의] 로컬 호스트 주소(http://localhost:8080)를 붙여야 이미지가 보일 수 있다. 
                      보통은 axios 셋팅이나 env에 설정된 VITE_API_URL을 활용한다. */}
                  <img src={p.imageUrl} alt={p.name} />
                  <div>
                    <p className="font-bold text-gray-800">{p.name}</p>
                    <p className="text-sm text-gray-500">가격: {p.price.toLocaleString()}원</p>
                    <p className="text-sm text-gray-500">
                      재고: <span className={p.stockQuantity === 0 ? "text-red-500 font-bold" : "text-green-600 font-bold"}>{p.stockQuantity}개</span>
                    </p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <button className="text-sm border border-gray-300 bg-white px-3 py-1 rounded hover:bg-gray-100 transition-colors">수정</button>
                  <button className="text-sm border border-red-200 bg-red-50 text-red-600 px-3 py-1 rounded hover:bg-red-100 transition-colors">삭제</button>
                </div>
              </div>
            ))}
            
            {products.length === 0 && (
              <p className="py-8 text-center text-gray-500">현재 등록된 포켓몬 상자가 없습니다.</p>
            )}
          </div>
        </section>

      </div>
    // </MainLayout>
  );
};

export default AdminPage;