import { Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WishlistService, WishlistItem } from '../../core/services/wishlist.service';
import { CartService } from '../../core/services/cart.service';
import { ProductService } from '../../core/services/product.service';
import { ToastService } from '../../core/services/toast.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-wishlist',
  imports: [DecimalPipe, RouterLink, TranslateModule],
  templateUrl: './wishlist.component.html',
  styleUrl: './wishlist.component.css',
})
export class WishlistComponent implements OnInit {

  constructor(
    protected wishlistService: WishlistService,
    private cartService: CartService,
    private productService: ProductService,
    private toastService: ToastService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.wishlistService.load();
  }

  getCartQuantity(productId: number): number {
    const item = this.cartService.items().find(i => i.product.id === productId);
    return item ? item.quantity : 0;
  }

  onQuantityInput(event: Event, productId: number, stock: number | null): void {
    const input = event.target as HTMLInputElement;
    const qty = input.valueAsNumber;
    if (!qty || qty <= 0) {
      this.cartService.removeFromCart(productId);
      return;
    }
    const capped = stock !== null ? Math.min(qty, stock) : qty;
    this.cartService.updateQuantity(productId, capped);
    if (capped !== qty) {
      input.value = String(capped);
    }
  }

  incrementCart(productId: number, stock: number | null): void {
    const qty = this.getCartQuantity(productId);
    if (stock !== null && qty >= stock) return;
    this.cartService.updateQuantity(productId, qty + 1);
  }

  decrementCart(productId: number): void {
    const qty = this.getCartQuantity(productId);
    if (qty <= 1) {
      this.cartService.removeFromCart(productId);
    } else {
      this.cartService.updateQuantity(productId, qty - 1);
    }
  }

  addToCart(item: WishlistItem): void {
    this.productService.getProductById(item.productId).subscribe((product) => {
      this.cartService.addToCart(product);
      this.toastService.show(`${item.productDisplayName} ${this.translate.instant('cart.addedToCart')}`);
    });
  }

  remove(productId: number): void {
    this.wishlistService.toggle(productId);
  }
}
