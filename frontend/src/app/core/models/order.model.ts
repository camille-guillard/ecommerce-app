export interface Order {
  id: number;
  createdAt: string;
  totalAmount: number;
  status: string;
  billingStreet: string | null;
  billingCity: string | null;
  billingPostalCode: string | null;
  shippingStreet: string | null;
  shippingCity: string | null;
  shippingPostalCode: string | null;
  lines: OrderLine[];
  events: OrderEvent[];
}

export interface OrderLine {
  productId: number;
  productDisplayName: string;
  productImageUrl: string | null;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  userRating: number | null;
  variantLabel: string | null;
}

export interface OrderEvent {
  status: string;
  createdAt: string;
}
