import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-floating-cart',
  imports: [RouterLink, TranslateModule],
  template: `
    <a routerLink="/cart" class="floating-cart" [class.has-items]="cartService.itemCount() > 0">
      <div class="floating-cart-inner">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
        @if (cartService.itemCount() > 0) {
          <span class="floating-badge">{{ cartService.itemCount() }}</span>
        }
      </div>
    </a>
  `,
  styles: [`
    .floating-cart {
      position: fixed;
      bottom: 1.5rem;
      right: 1.5rem;
      z-index: 90;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.3rem;
      text-decoration: none;
      transition: transform 0.2s;
    }

    .floating-cart:hover {
      transform: translateY(-3px);
    }

    .floating-cart-inner {
      position: relative;
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background-color: var(--primary);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 14px rgba(0, 0, 0, 0.25);
      transition: background-color 0.2s, box-shadow 0.2s;
    }

    .floating-cart:hover .floating-cart-inner {
      background-color: var(--primary-hover);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
    }

    .floating-badge {
      position: absolute;
      top: -4px;
      right: -4px;
      background-color: #ef4444;
      color: white;
      font-size: 0.7rem;
      font-weight: 700;
      padding: 0.15rem 0.4rem;
      border-radius: 999px;
      min-width: 1.2rem;
      text-align: center;
      border: 2px solid var(--bg-primary);
    }

  `],
})
export class FloatingCartComponent {
  constructor(protected cartService: CartService) {}
}
