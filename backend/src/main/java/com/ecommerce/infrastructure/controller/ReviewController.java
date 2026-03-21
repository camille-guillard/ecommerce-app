package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.Review;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.service.ReviewService;
import com.ecommerce.infrastructure.dto.CanReviewResponse;
import com.ecommerce.infrastructure.dto.CreateReviewRequest;
import com.ecommerce.infrastructure.dto.ReviewResponse;
import com.ecommerce.infrastructure.dto.ReviewSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId).stream()
                .map(ReviewResponse::fromEntity)
                .toList();
    }

    @GetMapping("/summary")
    public ReviewSummaryResponse getSummary(@PathVariable Long productId) {
        return reviewService.getReviewSummary(productId);
    }

    @GetMapping("/can-review")
    public ResponseEntity<CanReviewResponse> canReview(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(new CanReviewResponse(false, false));
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new CanReviewResponse(
                reviewService.canReview(productId, user.getId()),
                reviewService.hasReviewed(productId, user.getId())
        ));
    }

    @PostMapping
    public ResponseEntity<?> createReview(@PathVariable Long productId, @Valid @RequestBody CreateReviewRequest request,
                                          Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User user = (User) authentication.getPrincipal();
            Review review = reviewService.createReview(productId, user.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.fromEntity(review));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
