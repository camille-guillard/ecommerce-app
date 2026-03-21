import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { ReviewService } from '../../core/services/review.service';
import { AuthService } from '../../core/services/auth.service';
import { WishlistService } from '../../core/services/wishlist.service';
import { Product, ProductVariant } from '../../core/models/product.model';
import { Review, ReviewSummary, CanReviewResponse } from '../../core/models/review.model';
import { StarRatingComponent } from '../../shared/components/star-rating/star-rating.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-product-detail',
  imports: [DecimalPipe, DatePipe, RouterLink, FormsModule, StarRatingComponent, TranslateModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css',
})
export class ProductDetailComponent implements OnInit {
  product = signal<Product | null>(null);
  loading = signal(true);
  reviews = signal<Review[]>([]);
  summary = signal<ReviewSummary | null>(null);
  canReviewStatus = signal<CanReviewResponse>({ canReview: false, hasReviewed: false });

  variants = signal<ProductVariant[]>([]);
  availableColors = signal<string[]>([]);
  availableSizes = signal<string[]>([]);
  selectedColor = signal<string | null>(null);
  selectedSize = signal<string | null>(null);
  selectedVariant = signal<ProductVariant | null>(null);

  reviewRating = 0;
  reviewComment = '';
  submitting = signal(false);

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private toastService: ToastService,
    private reviewService: ReviewService,
    protected authService: AuthService,
    protected wishlistService: WishlistService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProductById(id).subscribe((product) => {
      this.product.set(product);
      this.loading.set(false);
      if (product.hasVariants) {
        this.productService.getVariants(id).subscribe((v) => {
          this.variants.set(v);
          const colors = [...new Set(v.map(x => x.color).filter(Boolean))] as string[];
          const sizes = [...new Set(v.map(x => x.size).filter(Boolean))] as string[];
          this.availableColors.set(colors);
          this.availableSizes.set(sizes);
          if (colors.length > 0) this.selectColor(colors[0]);
        });
      }
    });
    this.loadReviews(id);
  }

  selectColor(color: string): void {
    this.selectedColor.set(color);
    this.updateSelectedVariant();
  }

  selectSize(size: string): void {
    this.selectedSize.set(size);
    this.updateSelectedVariant();
  }

  isSizeAvailable(size: string): boolean {
    const color = this.selectedColor();
    return this.variants().some(v => v.color === color && v.size === size && v.stock > 0);
  }

  private updateSelectedVariant(): void {
    const color = this.selectedColor();
    const size = this.selectedSize();
    if (color && size) {
      const v = this.variants().find(x => x.color === color && x.size === size);
      this.selectedVariant.set(v ?? null);
    } else if (color && this.availableSizes().length === 0) {
      const v = this.variants().find(x => x.color === color && !x.size);
      this.selectedVariant.set(v ?? null);
    }
  }

  isPreorder(product: { releaseDate: string | null }): boolean {
    return !!product.releaseDate && new Date(product.releaseDate) > new Date();
  }

  getMaxStock(): number | null {
    const variant = this.selectedVariant();
    if (variant) return variant.stock;
    const p = this.product();
    return p ? p.stock : null;
  }

  addToCart(): void {
    const p = this.product();
    if (!p || !p.available) return;
    const variant = this.selectedVariant();
    const variantId = variant?.id ?? null;
    const maxStock = variant ? variant.stock : p.stock;
    if (maxStock !== null && this.getCartQuantity(p.id) >= maxStock) return;
    const label = variant ? [variant.color, variant.size].filter(Boolean).join(' / ') : undefined;
    this.cartService.addToCart(p, variantId, label, variant?.stock);
  }

  decrementCart(productId: number): void {
    const variantId = this.selectedVariant()?.id ?? null;
    const qty = this.getCartQuantity(productId);
    if (qty <= 1) {
      this.cartService.removeFromCart(productId, variantId);
    } else {
      this.cartService.updateQuantity(productId, qty - 1, variantId);
    }
  }

  onCartQuantityInput(event: Event, productId: number, stock: number | null): void {
    const input = event.target as HTMLInputElement;
    const qty = input.valueAsNumber;
    const variantId = this.selectedVariant()?.id ?? null;
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

  getCartQuantity(productId: number): number {
    const variantId = this.selectedVariant()?.id ?? null;
    return this.cartService.getQuantity(productId, variantId);
  }

  onRatingSelect(rating: number): void {
    this.reviewRating = rating;
  }

  submitReview(): void {
    const p = this.product();
    if (!p || !this.reviewRating || !this.reviewComment.trim()) return;

    this.submitting.set(true);
    this.reviewService.createReview(p.id, this.reviewRating, this.reviewComment.trim()).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toastService.show(this.translate.instant('product.reviewThanks'));
        this.reviewRating = 0;
        this.reviewComment = '';
        this.loadReviews(p.id);
      },
      error: () => {
        this.submitting.set(false);
      },
    });
  }

  getHistogramPercent(star: number): number {
    const s = this.summary();
    if (!s || s.count === 0) return 0;
    return Math.round(((s.histogram[star] || 0) / s.count) * 100);
  }

  private loadReviews(productId: number): void {
    this.reviewService.getReviews(productId).subscribe((r) => this.reviews.set(r));
    this.reviewService.getSummary(productId).subscribe((s) => this.summary.set(s));
    this.reviewService.canReview(productId).subscribe((c) => this.canReviewStatus.set(c));
  }

  wishlistAriaLabel(productId: number, displayName: string): string {
    const key = this.wishlistService.isInWishlist(productId) ? 'product.removeFromWishlist' : 'product.addToWishlist';
    return this.translate.instant(key, { name: displayName });
  }
}
