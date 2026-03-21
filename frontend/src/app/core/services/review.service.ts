import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CanReviewResponse, Review, ReviewSummary } from '../models/review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  constructor(private http: HttpClient) {}

  getReviews(productId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`/api/products/${productId}/reviews`);
  }

  getSummary(productId: number): Observable<ReviewSummary> {
    return this.http.get<ReviewSummary>(`/api/products/${productId}/reviews/summary`);
  }

  canReview(productId: number): Observable<CanReviewResponse> {
    return this.http.get<CanReviewResponse>(`/api/products/${productId}/reviews/can-review`);
  }

  createReview(productId: number, rating: number, comment: string): Observable<Review> {
    return this.http.post<Review>(`/api/products/${productId}/reviews`, { rating, comment });
  }
}
