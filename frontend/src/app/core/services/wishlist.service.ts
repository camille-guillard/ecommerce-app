import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface WishlistItem {
  productId: number;
  productDisplayName: string;
  productImageUrl: string;
  productPrice: number;
  productStock: number | null;
  productAvailable: boolean;
}

@Injectable({ providedIn: 'root' })
export class WishlistService {
  readonly items = signal<WishlistItem[]>([]);

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<WishlistItem[]>('/api/wishlist').subscribe((items) => {
      this.items.set(items);
    });
  }

  isInWishlist(productId: number): boolean {
    return this.items().some((i) => i.productId === productId);
  }

  toggle(productId: number): void {
    if (this.isInWishlist(productId)) {
      this.http.delete(`/api/wishlist/${productId}`).subscribe(() => {
        this.items.set(this.items().filter((i) => i.productId !== productId));
      });
    } else {
      this.http.post(`/api/wishlist/${productId}`, {}).subscribe(() => {
        this.load();
      });
    }
  }

  clear(): void {
    this.items.set([]);
  }
}
