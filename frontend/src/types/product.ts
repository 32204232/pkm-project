export interface Product {
  id: number;
  name: string;
  price: number;
  stockQuantity: number;
  imageUrl: string;
  category: string;
  series: string;      // 추가됨
  status: string;      // 추가됨
  releaseDate: string; // LocalDate는 문자열로 전달됩니다.
}