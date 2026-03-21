import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { importProvidersFrom } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { CartService } from './cart.service';
import { Product } from '../models/product.model';

const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => { store[key] = value; },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; },
  };
})();

Object.defineProperty(globalThis, 'localStorage', { value: localStorageMock, writable: true });

describe('CartService', () => {
  let service: CartService;

  const mockProduct: Product = {
    id: 1,
    name: 'pasta-barilla',
    displayName: 'Pâtes Barilla 500g',
    description: 'Pâtes italiennes',
    detailedDescription: null,
    price: 1.89,
    available: true,
    imageUrl: null,
    categoryId: 1,
    categoryDisplayName: 'Alimentaire',
    discountPercent: null,
    discountedPrice: null,
    averageRating: null,
    reviewCount: null,
    hasVariants: false,
    stock: 50,
    releaseDate: null,
  };

  const mockProduct2: Product = {
    id: 2,
    name: 'evian-1-5l',
    displayName: 'Evian 1.5L',
    description: 'Eau minérale',
    detailedDescription: null,
    price: 0.89,
    available: true,
    imageUrl: null,
    categoryId: 2,
    categoryDisplayName: 'Boissons',
    discountPercent: null,
    discountedPrice: null,
    averageRating: null,
    reviewCount: null,
    hasVariants: false,
    stock: 50,
    releaseDate: null,
  };

  beforeEach(() => {
    localStorageMock.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), importProvidersFrom(TranslateModule.forRoot())],
    });
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty cart', () => {
    expect(service.items()).toEqual([]);
    expect(service.itemCount()).toBe(0);
    expect(service.total()).toBe(0);
  });

  it('should add a product to cart', () => {
    service.addToCart(mockProduct);

    expect(service.items()).toHaveLength(1);
    expect(service.items()[0].product.id).toBe(1);
    expect(service.items()[0].quantity).toBe(1);
    expect(service.itemCount()).toBe(1);
  });

  it('should increment quantity when adding same product', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct);

    expect(service.items()).toHaveLength(1);
    expect(service.items()[0].quantity).toBe(2);
    expect(service.itemCount()).toBe(2);
  });

  it('should add different products separately', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);

    expect(service.items()).toHaveLength(2);
    expect(service.itemCount()).toBe(2);
  });

  it('should calculate total correctly', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);

    expect(service.total()).toBeCloseTo(1.89 * 2 + 0.89, 2);
  });

  it('should remove product from cart', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);
    service.removeFromCart(1);

    expect(service.items()).toHaveLength(1);
    expect(service.items()[0].product.id).toBe(2);
  });

  it('should update quantity', () => {
    service.addToCart(mockProduct);
    service.updateQuantity(1, 5);

    expect(service.items()[0].quantity).toBe(5);
    expect(service.itemCount()).toBe(5);
  });

  it('should remove item when quantity set to 0', () => {
    service.addToCart(mockProduct);
    service.updateQuantity(1, 0);

    expect(service.items()).toHaveLength(0);
  });

  it('should clear cart', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);
    service.clearCart();

    expect(service.items()).toHaveLength(0);
    expect(service.itemCount()).toBe(0);
    expect(service.total()).toBe(0);
  });

  it('should persist to localStorage', () => {
    service.addToCart(mockProduct);

    const stored = JSON.parse(localStorageMock.getItem('ecommerce-cart')!);
    expect(stored).toHaveLength(1);
    expect(stored[0].product.id).toBe(1);
  });
});
