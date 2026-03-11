export interface Product {
  id: number;
  name: string;        // 예: "Pokémon Card Game Scarlet & Violet - Black Bolt"
  nameKo?: string;     // 예: "스칼렛&바이올렛 확장팩 「블랙볼트」" (참고용)
  price: number;       // 기본 가격 (USD 기준 권장)
  stockQuantity: number;
  imageUrl: string;
  category: string;    // Expansion Pack, High Class Pack 등
}