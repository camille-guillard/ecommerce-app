package com.ecommerce.domain.service;

import com.ecommerce.domain.model.Review;
import com.ecommerce.infrastructure.dto.CreateReviewRequest;
import com.ecommerce.infrastructure.dto.ReviewSummaryResponse;

import java.util.List;

public interface ReviewService {

    List<Review> getReviewsByProductId(Long productId);

    ReviewSummaryResponse getReviewSummary(Long productId);

    Review createReview(Long productId, Long userId, CreateReviewRequest request);

    boolean canReview(Long productId, Long userId);

    boolean hasReviewed(Long productId, Long userId);
}
