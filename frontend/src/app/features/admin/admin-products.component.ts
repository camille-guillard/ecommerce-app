import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-products',
  imports: [DecimalPipe, FormsModule, TranslateModule],
  templateUrl: './admin-products.component.html',
  styleUrl: './admin-products.component.css',
})
export class AdminProductsComponent implements OnInit {
  products = signal<any[]>([]);
  categories = signal<any[]>([]);
  page = signal(0);
  totalPages = signal(0);
  sortField = signal('id');
  sortDir = signal<'asc' | 'desc'>('asc');

  showModal = signal(false);
  editing = signal<any>(null);
  form = {
    displayName: '',
    description: '',
    price: 0,
    stock: null as number | null,
    imageUrl: '',
    categoryId: 0,
    releaseDate: '',
    discountPercent: null as number | null,
    promoEndDate: '',
  };
  uploading = signal(false);

  deactivateTarget = signal<any>(null);
  deactivateConfirm = false;

  constructor(private adminService: AdminService, private toastService: ToastService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.load();
    this.adminService.getCategories().subscribe(cats => this.categories.set(cats));
  }

  load(): void {
    const sort = `${this.sortField()},${this.sortDir()}`;
    this.adminService.getProducts(this.page(), 50, sort).subscribe(res => {
      this.products.set(res.content);
      this.totalPages.set(res.totalPages);
    });
  }

  toggleSort(field: string): void {
    if (this.sortField() === field) {
      this.sortDir.set(this.sortDir() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDir.set('asc');
    }
    this.page.set(0);
    this.load();
  }

  sortIcon(field: string): string {
    if (this.sortField() !== field) return '↕';
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  openCreate(): void {
    this.editing.set(null);
    this.form = { displayName: '', description: '', price: 0, stock: 50, imageUrl: '', categoryId: 0, releaseDate: '', discountPercent: null, promoEndDate: '' };
    this.showModal.set(true);
  }

  openEdit(product: any): void {
    this.editing.set(product);
    this.form = {
      displayName: product.displayName,
      description: product.description || '',
      price: product.price,
      stock: product.stock,
      imageUrl: product.imageUrl || '',
      categoryId: product.categoryId,
      releaseDate: product.releaseDate || '',
      discountPercent: product.discountPercent || null,
      promoEndDate: product.promoEndDate || '',
    };
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  onFileSelect(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.uploading.set(true);
    this.adminService.uploadImage(file).subscribe({
      next: (res) => {
        this.form.imageUrl = res.url;
        this.uploading.set(false);
        this.toastService.show(this.translate.instant('admin.imageUploaded'));
      },
      error: () => {
        this.uploading.set(false);
        this.toastService.show(this.translate.instant('admin.uploadError'));
      },
    });
  }

  save(): void {
    const data: any = { ...this.form };
    if (!data.releaseDate) data.releaseDate = null;
    if (data.stock === '') data.stock = null;

    const editing = this.editing();
    if (editing) {
      this.adminService.updateProduct(editing.id, data).subscribe({
        next: () => {
          this.toastService.show(this.translate.instant('admin.productUpdated'));
          this.closeModal();
          this.load();
        },
        error: () => this.toastService.show(this.translate.instant('admin.error')),
      });
    } else {
      this.adminService.createProduct(data).subscribe({
        next: () => {
          this.toastService.show(this.translate.instant('admin.productCreated'));
          this.closeModal();
          this.load();
        },
        error: () => this.toastService.show(this.translate.instant('admin.error')),
      });
    }
  }

  openDeactivate(product: any): void {
    this.deactivateTarget.set(product);
    this.deactivateConfirm = false;
  }

  closeDeactivate(): void {
    this.deactivateTarget.set(null);
    this.deactivateConfirm = false;
  }

  confirmDeactivate(): void {
    const target = this.deactivateTarget();
    if (!target) return;
    this.adminService.toggleProductActive(target.id).subscribe({
      next: () => {
        this.toastService.show(this.translate.instant(target.active ? 'admin.productDeactivated' : 'admin.productReactivated'));
        this.closeDeactivate();
        this.load();
      },
      error: () => this.toastService.show(this.translate.instant('admin.error')),
    });
  }

  toggleActive(product: any): void {
    if (product.active) {
      this.openDeactivate(product);
    } else {
      this.adminService.toggleProductActive(product.id).subscribe({
        next: () => {
          this.toastService.show(this.translate.instant('admin.productReactivated'));
          this.load();
        },
        error: () => this.toastService.show(this.translate.instant('admin.error')),
      });
    }
  }

  goToPage(p: number): void {
    this.page.set(p);
    this.load();
  }
}
