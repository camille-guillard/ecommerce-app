export interface Product {
  id: number;
  name: string;
  displayName: string;
  description: string;
  detailedDescription: string | null;
  price: number;
  available: boolean;
  stock: number | null;
  imageUrl: string | null;
  categoryId: number;
  categoryDisplayName: string;
  discountPercent: number | null;
  discountedPrice: number | null;
  averageRating: number | null;
  reviewCount: number | null;
  hasVariants: boolean;
  releaseDate: string | null;
}

export interface ProductVariant {
  id: number;
  color: string | null;
  size: string | null;
  stock: number;
  priceOverride: number | null;
}
