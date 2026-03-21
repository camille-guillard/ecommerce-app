import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { AuthService } from '../../core/services/auth.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-cart',
  imports: [RouterLink, DecimalPipe, FormsModule, TranslateModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent implements OnInit {
  processing = signal(false);
  billingStreet = '';
  billingCity = '';
  billingPostalCode = '';
  shippingStreet = '';
  shippingCity = '';
  shippingPostalCode = '';

  constructor(
    protected cartService: CartService,
    private orderService: OrderService,
    private toastService: ToastService,
    protected authService: AuthService,
    private router: Router,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.billingStreet = user.street || '';
      this.billingCity = user.city || '';
      this.billingPostalCode = user.postalCode || '';
      this.shippingStreet = user.street || '';
      this.shippingCity = user.city || '';
      this.shippingPostalCode = user.postalCode || '';
    }
  }

  increment(productId: number, currentQty: number, stock: number | null, variantId?: number | null): void {
    if (stock !== null && currentQty >= stock) return;
    this.cartService.updateQuantity(productId, currentQty + 1, variantId);
  }

  decrement(productId: number, currentQty: number, variantId?: number | null): void {
    this.cartService.updateQuantity(productId, currentQty - 1, variantId);
  }

  onQuantityInput(event: Event, productId: number, stock: number | null, variantId?: number | null): void {
    const input = event.target as HTMLInputElement;
    const qty = input.valueAsNumber;
    if (!qty || qty <= 0) {
      this.cartService.removeFromCart(productId, variantId);
      return;
    }
    const capped = stock !== null ? Math.min(qty, stock) : qty;
    this.cartService.updateQuantity(productId, capped, variantId);
    if (capped !== qty) {
      input.value = String(capped);
    }
  }

  remove(productId: number, variantId?: number | null): void {
    this.cartService.removeFromCart(productId, variantId);
  }

  clear(): void {
    this.cartService.clearCart();
  }

  checkout(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { redirect: '/cart' } });
      return;
    }

    const items = this.cartService.items().map((item) => ({
      productId: item.product.id,
      quantity: item.quantity,
      unitPrice: item.product.discountedPrice ?? item.product.price,
      variantId: item.variantId ?? null,
    }));

    this.processing.set(true);

    this.orderService.createOrder({
      items,
      billingStreet: this.billingStreet,
      billingCity: this.billingCity,
      billingPostalCode: this.billingPostalCode,
      shippingStreet: this.shippingStreet,
      shippingCity: this.shippingCity,
      shippingPostalCode: this.shippingPostalCode,
    }).subscribe({
      next: () => {
        setTimeout(() => {
          this.cartService.clearCart();
          this.processing.set(false);
          this.toastService.show(this.translate.instant('cart.orderConfirmed'));
          this.router.navigate(['/orders']);
        }, 3000);
      },
      error: (err) => {
        this.processing.set(false);
        if (err.error?.error === 'INSUFFICIENT_STOCK') {
          this.toastService.show(this.translate.instant('cart.stockError', { name: err.error.productName, stock: err.error.availableStock }));
        } else {
          this.toastService.show(this.translate.instant('cart.payError'));
        }
      },
    });
  }
}
