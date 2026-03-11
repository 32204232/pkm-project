import React, { type ReactNode } from 'react';
import Navbar from '../Navbar'; // [핵심] 현재 위치(layout/)에서 한 단계 위(components/)로 가서 Navbar를 가져옵니다.

interface Props {
  children: ReactNode;
}

const MainLayout = ({ children }: Props) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      {/* 각 페이지 컴포넌트(children)가 렌더링되는 중앙 영역 */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {children}
      </main>
    </div>
  );
};

export default MainLayout;