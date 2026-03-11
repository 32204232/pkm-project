import React from 'react';
import Navbar from '../Navbar';

interface Props {
  children: React.ReactNode;
}

const MainLayout = ({ children }: Props) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      {/* 페이지별 내용이 들어갈 자리 */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {children}
      </main>
    </div>
  );
};

export default MainLayout;