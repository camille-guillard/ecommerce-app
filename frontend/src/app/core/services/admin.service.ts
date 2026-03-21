import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  getUsers(page = 0, size = 20, sort = 'id,asc'): Observable<Page<any>> {
    return this.http.get<Page<any>>(`/api/admin/users?page=${page}&size=${size}&sort=${sort}`);
  }

  getOrders(page = 0, size = 20, sort = 'createdAt,desc'): Observable<Page<any>> {
    return this.http.get<Page<any>>(`/api/admin/orders?page=${page}&size=${size}&sort=${sort}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(`/api/admin/orders/${orderId}/status`, { status });
  }

  getProducts(page = 0, size = 20, sort = 'id,asc'): Observable<Page<any>> {
    return this.http.get<Page<any>>(`/api/admin/products?page=${page}&size=${size}&sort=${sort}`);
  }

  updateProduct(id: number, data: any): Observable<any> {
    return this.http.put(`/api/admin/products/${id}`, data);
  }

  createProduct(data: any): Observable<any> {
    return this.http.post('/api/admin/products', data);
  }

  toggleProductActive(id: number): Observable<any> {
    return this.http.put(`/api/admin/products/${id}/toggle-active`, {});
  }

  uploadImage(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>('/api/admin/upload', formData);
  }

  getCategories(): Observable<any[]> {
    return this.http.get<any[]>('/api/admin/categories');
  }
}
