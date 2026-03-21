import { Component, OnInit, signal } from '@angular/core';
import { AdminService } from '../../core/services/admin.service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-users',
  imports: [TranslateModule],
  template: `
    <div class="admin-section">
      <table class="admin-table">
        <thead>
          <tr>
            <th class="sortable" (click)="toggleSort('id')">ID <span class="sort-icon">{{ sortIcon('id') }}</span></th>
            <th class="sortable" (click)="toggleSort('username')">{{ 'admin.username' | translate }} <span class="sort-icon">{{ sortIcon('username') }}</span></th>
            <th class="sortable" (click)="toggleSort('email')">{{ 'admin.email' | translate }} <span class="sort-icon">{{ sortIcon('email') }}</span></th>
            <th class="sortable" (click)="toggleSort('firstName')">{{ 'admin.firstName' | translate }} <span class="sort-icon">{{ sortIcon('firstName') }}</span></th>
            <th class="sortable" (click)="toggleSort('lastName')">{{ 'admin.lastName' | translate }} <span class="sort-icon">{{ sortIcon('lastName') }}</span></th>
            <th class="sortable" (click)="toggleSort('city')">{{ 'admin.city' | translate }} <span class="sort-icon">{{ sortIcon('city') }}</span></th>
            <th>{{ 'admin.roles' | translate }}</th>
          </tr>
        </thead>
        <tbody>
          @for (user of users(); track user.id) {
            <tr>
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.firstName }}</td>
              <td>{{ user.lastName }}</td>
              <td>{{ user.city }}</td>
              <td>
                @for (role of user.roles; track role) {
                  <span class="role-badge" [class.admin]="role === 'ADMIN'">{{ role }}</span>
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
    .role-badge { display: inline-block; padding: 0.1rem 0.4rem; border-radius: 4px; font-size: 0.7rem; font-weight: 600; background-color: var(--bg-secondary); color: var(--text-secondary); margin-right: 0.2rem; }
    .role-badge.admin { background-color: #ef444420; color: #ef4444; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; margin-top: 1rem; }
    .pagination button { padding: 0.4rem 0.8rem; border: 1px solid var(--border-color); border-radius: 6px; background: var(--bg-card); color: var(--text-primary); cursor: pointer; }
    .pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
  `],
})
export class AdminUsersComponent implements OnInit {
  users = signal<any[]>([]);
  page = signal(0);
  totalPages = signal(0);
  sortField = signal('id');
  sortDir = signal<'asc' | 'desc'>('asc');

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const sort = `${this.sortField()},${this.sortDir()}`;
    this.adminService.getUsers(this.page(), 20, sort).subscribe(res => {
      this.users.set(res.content);
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

  goToPage(p: number): void {
    this.page.set(p);
    this.load();
  }
}
