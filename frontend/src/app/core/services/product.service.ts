import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { Product, ProductVariant } from '../models/product.model';
import { LanguageService } from './language.service';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly apiUrl = '/api/products';
  private readonly langService = inject(LanguageService);

  constructor(private http: HttpClient) {}

  getProducts(params: {
    categoryId?: number;
    search?: string;
    onPromotion?: boolean;
    availableOnly?: boolean;
    preorderOnly?: boolean;
    minRating?: number;
    attributes?: Record<string, string>;
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<Page<Product>> {
    let httpParams = new HttpParams().set('lang', this.langService.currentLang());

    if (params.categoryId) {
      httpParams = httpParams.set('categoryId', params.categoryId.toString());
    }
    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    if (params.onPromotion) {
      httpParams = httpParams.set('onPromotion', 'true');
    }
    if (params.availableOnly) {
      httpParams = httpParams.set('availableOnly', 'true');
    }
    if (params.preorderOnly) {
      httpParams = httpParams.set('preorderOnly', 'true');
    }
    if (params.minRating) {
      httpParams = httpParams.set('minRating', params.minRating.toString());
    }
    if (params.page !== undefined) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.size !== undefined) {
      httpParams = httpParams.set('size', params.size.toString());
    }
    if (params.attributes) {
      Object.entries(params.attributes).forEach(([key, value]) => {
        httpParams = httpParams.set(`attr_${key}`, value);
      });
    }
    if (params.sort) {
      httpParams = httpParams.set('sort', params.sort);
    }

    return this.http.get<Page<Product>>(this.apiUrl, { params: httpParams });
  }

  getVariants(productId: number): Observable<ProductVariant[]> {
    return this.http.get<ProductVariant[]>(`${this.apiUrl}/${productId}/variants`);
  }

  getAvailableAttributes(categoryId: number): Observable<Record<string, string[]>> {
    return this.http.get<Record<string, string[]>>(`${this.apiUrl}/attributes`, {
      params: new HttpParams().set('categoryId', categoryId.toString()),
    });
  }

  getProductById(id: number): Observable<Product> {
    const params = new HttpParams().set('lang', this.langService.currentLang());
    return this.http.get<Product>(`${this.apiUrl}/${id}`, { params });
  }
}
