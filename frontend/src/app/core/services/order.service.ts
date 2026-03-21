import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';
import { LanguageService } from './language.service';

export interface CreateOrderRequest {
  items: { productId: number; quantity: number; unitPrice: number }[];
  billingStreet?: string;
  billingCity?: string;
  billingPostalCode?: string;
  shippingStreet?: string;
  shippingCity?: string;
  shippingPostalCode?: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly apiUrl = '/api/orders';

  constructor(private http: HttpClient, private langService: LanguageService) {}

  getOrders(): Observable<Order[]> {
    const params = new HttpParams().set('lang', this.langService.currentLang());
    return this.http.get<Order[]>(this.apiUrl, { params });
  }

  getOrderById(id: number): Observable<Order> {
    const params = new HttpParams().set('lang', this.langService.currentLang());
    return this.http.get<Order>(`${this.apiUrl}/${id}`, { params });
  }

  createOrder(request: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, request);
  }
}
