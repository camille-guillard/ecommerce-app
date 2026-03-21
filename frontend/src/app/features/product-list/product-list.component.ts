import { Component, OnInit, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StarRatingComponent } from '../../shared/components/star-rating/star-rating.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { WishlistService } from '../../core/services/wishlist.service';
import { AuthService } from '../../core/services/auth.service';
import { Product, ProductVariant } from '../../core/models/product.model';
import { Category } from '../../core/models/category.model';
import { Page } from '../../core/models/page.model';

const CATEGORY_ICONS: Record<string, { icon: string; color: string }> = {
  food: { icon: '&#x1F6D2;', color: '#f59e0b' },
  drinks: { icon: '&#x1F4A7;', color: '#06b6d4' },
  alcohol: { icon: '&#x1F377;', color: '#7c3aed' },
  clothing: { icon: '&#x1F455;', color: '#ec4899' },
  multimedia: { icon: '&#x1F4BB;', color: '#3b82f6' },
  videogames: { icon: '&#x1F3AE;', color: '#10b981' },
  household: { icon: '&#x1F9F9;', color: '#8b5cf6' },
  beauty: { icon: '&#x2728;', color: '#f43f5e' },
  toys: { icon: '&#x1F9F8;', color: '#f97316' },
  books: { icon: '&#x1F4DA;', color: '#6366f1' },
};

@Component({
  selector: 'app-product-list',
  imports: [FormsModule, DecimalPipe, RouterLink, StarRatingComponent, TranslateModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);
  loading = signal(false);

  selectedCategoryId = signal<number | null>(null);
  searchTerm = signal('');
  sortOption = signal('');
  onPromotion = signal(false);
  availableOnly = signal(false);
  preorderOnly = signal(false);
  minRating = signal(0);
  availableAttributes = signal<Record<string, string[]>>({});
  selectedAttributes = signal<Record<string, string>>({});
  pageSize = 12;

  popoverProductId = signal<number | null>(null);
  popoverVariants = signal<ProductVariant[]>([]);
  popoverColors = signal<string[]>([]);
  popoverSizes = signal<string[]>([]);
  popoverSelectedColor = signal<string | null>(null);
  popoverSelectedSize = signal<string | null>(null);

  pages = computed(() => Array.from({ length: this.totalPages() }, (_, i) => i));

  private searchSubject = new Subject<string>();

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    protected cartService: CartService,
    private toastService: ToastService,
    protected wishlistService: WishlistService,
    protected authService: AuthService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((cats) => this.categories.set(cats));
    this.loadProducts();

    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.currentPage.set(0);
        this.loadProducts();
      });
  }

  onSearchInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchSubject.next(value);
  }

  clearSearch(input: HTMLInputElement): void {
    input.value = '';
    this.searchSubject.next('');
  }

  onCategoryChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedCategoryId.set(value ? Number(value) : null);
    this.currentPage.set(0);
    this.loadProducts();
  }

  onSortChange(event: Event): void {
    this.sortOption.set((event.target as HTMLSelectElement).value);
    this.currentPage.set(0);
    this.loadProducts();
  }

  onPromotionChange(event: Event): void {
    this.onPromotion.set((event.target as HTMLInputElement).checked);
    this.currentPage.set(0);
    this.loadProducts();
  }

  selectCategory(categoryId: number | null): void {
    this.selectedCategoryId.set(categoryId);
    this.selectedAttributes.set({});
    this.currentPage.set(0);
    this.loadAttributes(categoryId);
    this.loadProducts();
  }

  onAttributeChange(key: string, value: string): void {
    const attrs = { ...this.selectedAttributes() };
    if (value) {
      attrs[key] = value;
    } else {
      delete attrs[key];
    }
    this.selectedAttributes.set(attrs);
    this.currentPage.set(0);
    this.loadProducts();
  }

  get attributeKeys(): string[] {
    return Object.keys(this.availableAttributes());
  }

  private loadAttributes(categoryId: number | null): void {
    if (categoryId) {
      this.productService.getAvailableAttributes(categoryId).subscribe((attrs) => {
        this.availableAttributes.set(attrs);
      });
    } else {
      this.availableAttributes.set({});
    }
  }

  isCategoryChildSelected(cat: Category): boolean {
    const selected = this.selectedCategoryId();
    if (!selected || !cat.children) return false;
    return cat.children.some(child => child.id === selected);
  }

  setMinRating(rating: number): void {
    this.minRating.set(rating);
    this.currentPage.set(0);
    this.loadProducts();
  }

  onAvailableOnlyChange(event: Event): void {
    this.availableOnly.set((event.target as HTMLInputElement).checked);
    this.currentPage.set(0);
    this.loadProducts();
  }

  onPreorderChange(event: Event): void {
    this.preorderOnly.set((event.target as HTMLInputElement).checked);
    this.currentPage.set(0);
    this.loadProducts();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadProducts();
  }

  addToCart(product: Product): void {
    if (!product.available) return;
    if (product.hasVariants) {
      this.openPopover(product);
      return;
    }
    if (product.stock !== null && this.getCartQuantity(product.id) >= product.stock) return;
    this.cartService.addToCart(product);
  }

  openPopover(product: Product): void {
    if (this.popoverProductId() === product.id) {
      this.closePopover();
      return;
    }
    this.popoverProductId.set(product.id);
    this.popoverSelectedColor.set(null);
    this.popoverSelectedSize.set(null);
    this.productService.getVariants(product.id).subscribe((v) => {
      this.popoverVariants.set(v);
      const colors = [...new Set(v.map(x => x.color).filter(Boolean))] as string[];
      const sizes = [...new Set(v.map(x => x.size).filter(Boolean))] as string[];
      this.popoverColors.set(colors);
      this.popoverSizes.set(sizes);
      if (colors.length > 0) this.popoverSelectedColor.set(colors[0]);
    });
  }

  closePopover(): void {
    this.popoverProductId.set(null);
  }

  popoverSelectColor(color: string): void {
    this.popoverSelectedColor.set(color);
  }

  popoverSelectSize(size: string): void {
    this.popoverSelectedSize.set(size);
  }

  popoverIsSizeAvailable(size: string): boolean {
    const color = this.popoverSelectedColor();
    return this.popoverVariants().some(v => v.color === color && v.size === size && v.stock > 0);
  }

  popoverAddToCart(product: Product): void {
    const color = this.popoverSelectedColor();
    const size = this.popoverSelectedSize();
    const variant = this.popoverVariants().find(v => v.color === color && v.size === size);
    const variantId = variant?.id ?? null;
    const label = [color, size].filter(Boolean).join(' / ');
    this.cartService.addToCart(product, variantId, label || undefined, variant?.stock);
    this.closePopover();
  }

  decrementCart(productId: number): void {
    const qty = this.getCartQuantity(productId);
    if (qty <= 1) {
      this.cartService.removeFromCart(productId);
    } else {
      this.cartService.updateQuantity(productId, qty - 1);
    }
  }

  onCartQuantityInput(event: Event, productId: number, stock: number | null): void {
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

  getCartQuantity(productId: number): number {
    const item = this.cartService.items().find(i => i.product.id === productId);
    return item ? item.quantity : 0;
  }

  getCategoryStyle(product: Product): string {
    const cat = this.categories().find((c) => c.id === product.categoryId);
    const info = cat ? CATEGORY_ICONS[cat.name] : null;
    return info ? `background-color: ${info.color}20; color: ${info.color}` : '';
  }

  getCategoryIcon(product: Product): string {
    const cat = this.categories().find((c) => c.id === product.categoryId);
    const info = cat ? CATEGORY_ICONS[cat.name] : null;
    return info ? info.icon : '&#x1F4E6;';
  }

  getPlaceholderBg(product: Product): string {
    const cat = this.categories().find((c) => c.id === product.categoryId);
    const info = cat ? CATEGORY_ICONS[cat.name] : null;
    return info ? info.color + '15' : '#f3f4f6';
  }

  getPlaceholderIcon(product: Product): string {
    return this.getCategoryIcon(product);
  }

  isPreorder(product: Product): boolean {
    return !!product.releaseDate && new Date(product.releaseDate) > new Date();
  }

  wishlistAriaLabel(product: Product): string {
    const key = this.wishlistService.isInWishlist(product.id) ? 'product.removeFromWishlist' : 'product.addToWishlist';
    return this.translate.instant(key, { name: product.displayName });
  }

  private loadProducts(): void {
    this.loading.set(true);
    this.productService
      .getProducts({
        categoryId: this.selectedCategoryId() ?? undefined,
        search: this.searchTerm() || undefined,
        onPromotion: this.onPromotion() || undefined,
        availableOnly: this.availableOnly() || undefined,
        preorderOnly: this.preorderOnly() || undefined,
        minRating: this.minRating() || undefined,
        attributes: Object.keys(this.selectedAttributes()).length > 0 ? this.selectedAttributes() : undefined,
        page: this.currentPage(),
        size: this.pageSize,
        sort: this.sortOption() || undefined,
      })
      .subscribe((page: Page<Product>) => {
        this.products.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      });
  }
}
