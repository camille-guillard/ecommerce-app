export interface Review {
  id: number;
  username: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface ReviewSummary {
  averageRating: number;
  count: number;
  histogram: Record<number, number>;
}

export interface CanReviewResponse {
  canReview: boolean;
  hasReviewed: boolean;
}
