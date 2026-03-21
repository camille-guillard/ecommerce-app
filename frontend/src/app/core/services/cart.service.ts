import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CartItem } from '../models/cart-item.model';
import { Product } from '../models/product.model';
import { ProductService } from './product.service';
import { forkJoin } from 'rxjs';

interface CartSyncRequest {
  items: { productId: number; quantity: number; variantId?: number | null }[];
}

interface CartResponse {
  items: { productId: number; quantity: number; variantId?: number | null }[];
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly STORAGE_KEY = 'ecommerce-cart';
  private readonly _items = signal<CartItem[]>(this.loadFromStorage());
  private readonly http = inject(HttpClient);
  private readonly productService = inject(ProductService);

  private _isLoggedIn = false;

  readonly items = this._items.asReadonly();
  readonly itemCount = computed(() =>
    this._items().reduce((sum, item) => sum + item.quantity, 0)
  );
  readonly total = computed(() =>
    this._items().reduce((sum, item) => sum + (item.product.discountedPrice ?? item.product.price) * item.quantity, 0)
  );

  setLoggedIn(loggedIn: boolean): void {
    this._isLoggedIn = loggedIn;
  }

  private itemKey(productId: number, variantId?: number | null): string {
    return variantId ? `${productId}-${variantId}` : `${productId}`;
  }

  private matchItem(item: CartItem, productId: number, variantId?: number | null): boolean {
    return item.product.id === productId && (item.variantId ?? null) === (variantId ?? null);
  }

  addToCart(product: Product, variantId?: number | null, variantLabel?: string, variantStock?: number | null): void {
    const items = this._items();
    const existing = items.find(i => this.matchItem(i, product.id, variantId));

    if (existing) {
      this._items.set(
        items.map(item =>
          this.matchItem(item, product.id, variantId)
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      );
    } else {
      this._items.set([...items, { product, quantity: 1, variantId, variantLabel, variantStock }]);
    }
    this.saveAndSync();
  }

  removeFromCart(productId: number, variantId?: number | null): void {
    this._items.set(this._items().filter(item => !this.matchItem(item, productId, variantId)));
    this.saveAndSync();
  }

  updateQuantity(productId: number, quantity: number, variantId?: number | null): void {
    if (quantity <= 0) {
      this.removeFromCart(productId, variantId);
      return;
    }
    this._items.set(
      this._items().map(item =>
        this.matchItem(item, productId, variantId) ? { ...item, quantity } : item
      )
    );
    this.saveAndSync();
  }

  getQuantity(productId: number, variantId?: number | null): number {
    const item = this._items().find(i => this.matchItem(i, productId, variantId));
    return item ? item.quantity : 0;
  }

  clearCart(): void {
    this._items.set([]);
    this.saveAndSync();
  }

  mergeOnLogin(): void {
    const localItems = this._items();
    const request: CartSyncRequest = {
      items: localItems.map(i => ({ productId: i.product.id, quantity: i.quantity, variantId: i.variantId })),
    };

    this.http.post<CartResponse>('/api/cart/merge', request).subscribe((res) => {
      this.loadCartFromResponse(res);
      localStorage.removeItem(this.STORAGE_KEY);
    });
  }

  clearOnLogout(): void {
    this._items.set([]);
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private loadCartFromResponse(res: CartResponse): void {
    if (res.items.length === 0) {
      this._items.set([]);
      return;
    }

    const productIds = [...new Set(res.items.map(i => i.productId))];
    const requests = productIds.map(id => this.productService.getProductById(id));

    forkJoin(requests).subscribe((products) => {
      const cartItems: CartItem[] = [];
      for (const resItem of res.items) {
        const product = products.find(p => p.id === resItem.productId);
        if (product) {
          cartItems.push({ product, quantity: resItem.quantity, variantId: resItem.variantId });
        }
      }
      this._items.set(cartItems);
    });
  }

  private saveAndSync(): void {
    if (this._isLoggedIn) {
      const request: CartSyncRequest = {
        items: this._items().map(i => ({ productId: i.product.id, quantity: i.quantity, variantId: i.variantId })),
      };
      this.http.put<CartResponse>('/api/cart', request).subscribe();
    }
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this._items()));
  }

  private loadFromStorage(): CartItem[] {
    if (typeof window === 'undefined') return [];
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }
}
