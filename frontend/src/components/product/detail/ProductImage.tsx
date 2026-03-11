import React from 'react';

interface Props {
  src: string;
  alt: string;
}

const ProductImage = ({ src, alt }: Props) => (
  <div className="flex-1 bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
    <img 
      src={src} 
      alt={alt} 
      className="w-full h-auto object-contain max-h-[500px]"
    />
  </div>
);

export default ProductImage;