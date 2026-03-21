import { Product } from './product.model';

export interface CartItem {
  product: Product;
  quantity: number;
  variantId?: number | null;
  variantLabel?: string;
  variantStock?: number | null;
}
