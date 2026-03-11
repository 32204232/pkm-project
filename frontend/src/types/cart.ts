export interface CartAddRequest {
  productId: number;
  count: number;
}

export interface CartResponse {
  cartItemId: number;
  productId: number;
  productName: string;
  price: number;
  count: number;
  imageUrl: string;
}