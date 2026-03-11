import React from 'react';

interface Props {
  name: string;
  price: number;
}

const ProductInfo = ({ name, price }: Props) => (
  <div className="space-y-4">
    <nav className="flex text-sm text-gray-400">
      <span>Home</span> <span className="mx-2">&gt;</span> <span>Booster Packs</span>
    </nav>
    <h1 className="text-4xl font-black text-gray-900 leading-tight">
      {name}
    </h1>
    <p className="text-blue-600 font-bold tracking-widest text-sm uppercase">
      Korean Version - Official Sealed
    </p>
    <div className="flex items-end gap-3 pt-4">
      <span className="text-5xl font-black text-red-600">${price}</span>
      <span className="text-gray-400 mb-2 font-medium uppercase text-sm">usd</span>
    </div>
  </div>
);

export default ProductInfo;