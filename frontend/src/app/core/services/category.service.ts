import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category.model';
import { LanguageService } from './language.service';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly apiUrl = '/api/categories';
  private readonly langService = inject(LanguageService);

  constructor(private http: HttpClient) {}

  getCategories(): Observable<Category[]> {
    const params = new HttpParams().set('lang', this.langService.currentLang());
    return this.http.get<Category[]>(this.apiUrl, { params });
  }
}
