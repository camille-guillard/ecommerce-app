import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../core/services/admin.service';
import { ToastService } from '../../core/services/toast.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-orders',
  imports: [DecimalPipe, DatePipe, RouterLink, TranslateModule],
  template: `
    <div class="admin-section">
      <table class="admin-table">
        <thead>
          <tr>
            <th class="sortable" (click)="toggleSort('id')">ID <span class="sort-icon">{{ sortIcon('id') }}</span></th>
            <th class="sortable" (click)="toggleSort('createdAt')">{{ 'admin.date' | translate }} <span class="sort-icon">{{ sortIcon('createdAt') }}</span></th>
            <th>{{ 'admin.user' | translate }}</th>
            <th class="sortable" (click)="toggleSort('totalAmount')">{{ 'admin.total' | translate }} <span class="sort-icon">{{ sortIcon('totalAmount') }}</span></th>
            <th class="sortable" (click)="toggleSort('status')">{{ 'admin.status' | translate }} <span class="sort-icon">{{ sortIcon('status') }}</span></th>
            <th>{{ 'admin.actions' | translate }}</th>
          </tr>
        </thead>
        <tbody>
          @for (order of orders(); track order.id) {
            <tr>
              <td><a [routerLink]="['/orders', order.id]" class="order-link">#{{ order.id }}</a></td>
              <td>{{ order.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
              <td>{{ order.username || '-' }}</td>
              <td>{{ order.totalAmount | number:'1.2-2' }} &euro;</td>
              <td>
                <span class="status-badge" [attr.data-status]="order.status">{{ statusLabel(order.status) }}</span>
              </td>
              <td class="actions-cell">
                @if (order.status === 'CONFIRMED') {
                  <button class="action-btn transit" (click)="changeStatus(order.id, 'IN_TRANSIT')">{{ 'admin.inTransit' | translate }}</button>
                  <button class="action-btn cancel" (click)="changeStatus(order.id, 'CANCELLED')">{{ 'admin.cancelOrder' | translate }}</button>
                }
                @if (order.status === 'IN_TRANSIT') {
                  <button class="action-btn complete" (click)="changeStatus(order.id, 'COMPLETED')">{{ 'admin.complete' | translate }}</button>
                }
                @if (order.status === 'COMPLETED' || order.status === 'CANCELLED') {
                  <span class="no-action">-</span>
                }
              </td>
            </tr>
          }
        </tbody>
      </table>
      @if (totalPages() > 1) {
        <div class="pagination">
          <button [disabled]="page() === 0" (click)="goToPage(page() - 1)">&laquo;</button>
          <span>{{ page() + 1 }} / {{ totalPages() }}</span>
          <button [disabled]="page() >= totalPages() - 1" (click)="goToPage(page() + 1)">&raquo;</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .admin-section { overflow-x: auto; }
    .sortable { cursor: pointer; user-select: none; white-space: nowrap; }
    .sortable:hover { color: var(--primary); }
    .sort-icon { font-size: 0.7rem; opacity: 0.6; }
    .admin-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
    .admin-table th, .admin-table td { padding: 0.6rem 0.8rem; text-align: left; border-bottom: 1px solid var(--border-color); }
    .admin-table th { font-weight: 700; color: var(--text-secondary); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; background-color: var(--bg-secondary); }
    .admin-table tr:hover { background-color: var(--bg-secondary); }
    .status-badge { display: inline-block; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .status-badge[data-status="CONFIRMED"] { background-color: #3b82f620; color: #3b82f6; }
    .status-badge[data-status="IN_TRANSIT"] { background-color: #f59e0b20; color: #f59e0b; }
    .status-badge[data-status="COMPLETED"] { background-color: #16a34a20; color: #16a34a; }
    .status-badge[data-status="CANCELLED"] { background-color: #ef444420; color: #ef4444; }
    .actions-cell { white-space: nowrap; }
    .action-btn { padding: 0.25rem 0.5rem; border: none; border-radius: 4px; font-size: 0.75rem; font-weight: 600; cursor: pointer; margin-right: 0.25rem; }
    .action-btn.transit { background-color: #f59e0b20; color: #f59e0b; }
    .action-btn.transit:hover { background-color: #f59e0b; color: white; }
    .action-btn.cancel { background-color: #ef444420; color: #ef4444; }
    .action-btn.cancel:hover { background-color: #ef4444; color: white; }
    .action-btn.complete { background-color: #16a34a20; color: #16a34a; }
    .action-btn.complete:hover { background-color: #16a34a; color: white; }
    .order-link { color: var(--primary); font-weight: 600; text-decoration: none; }
    .order-link:hover { text-decoration: underline; }
    .no-action { color: var(--text-secondary); font-size: 0.8rem; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; margin-top: 1rem; }
    .pagination button { padding: 0.4rem 0.8rem; border: 1px solid var(--border-color); border-radius: 6px; background: var(--bg-card); color: var(--text-primary); cursor: pointer; }
    .pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
  `],
})
export class AdminOrdersComponent implements OnInit {
  orders = signal<any[]>([]);
  page = signal(0);
  totalPages = signal(0);
  sortField = signal('createdAt');
  sortDir = signal<'asc' | 'desc'>('desc');

  constructor(private adminService: AdminService, private toastService: ToastService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const sort = `${this.sortField()},${this.sortDir()}`;
    this.adminService.getOrders(this.page(), 20, sort).subscribe(res => {
      this.orders.set(res.content);
      this.totalPages.set(res.totalPages);
    });
  }

  changeStatus(orderId: number, status: string): void {
    this.adminService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        this.toastService.show(this.translate.instant('admin.statusUpdated'));
        this.load();
      },
      error: (err) => this.toastService.show(this.translate.instant('admin.statusUpdateError', { detail: err.error || 'Error' })),
    });
  }

  statusLabel(status: string): string {
    return this.translate.instant('orders.status.' + status);
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

  goToPage(p: number): void {
    this.page.set(p);
    this.load();
  }
}
