import { Component, signal } from '@angular/core';
import { AdminUsersComponent } from './admin-users.component';
import { AdminOrdersComponent } from './admin-orders.component';
import { AdminProductsComponent } from './admin-products.component';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-admin',
  imports: [AdminUsersComponent, AdminOrdersComponent, AdminProductsComponent, TranslateModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent {
  activeTab = signal<'orders' | 'products' | 'users'>('orders');

  setTab(tab: 'orders' | 'products' | 'users'): void {
    this.activeTab.set(tab);
  }
}
