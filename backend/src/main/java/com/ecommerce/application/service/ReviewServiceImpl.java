package com.ecommerce.application.service;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.Review;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.ReviewRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.domain.service.ReviewService;
import com.ecommerce.infrastructure.dto.CreateReviewRequest;
import com.ecommerce.infrastructure.dto.ReviewSummaryResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository,
                             ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public ReviewSummaryResponse getReviewSummary(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        Long count = reviewRepository.countByProductId(productId);
        List<Object[]> histData = reviewRepository.findRatingHistogramByProductId(productId);

        Map<Integer, Long> histogram = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            histogram.put(i, 0L);
        }
        for (Object[] row : histData) {
            histogram.put((Integer) row[0], (Long) row[1]);
        }

        return new ReviewSummaryResponse(
                avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
                count != null ? count : 0L,
                histogram
        );
    }

    @Override
    public Review createReview(Long productId, Long userId, CreateReviewRequest request) {
        if (!canReview(productId, userId)) {
            throw new IllegalArgumentException("You must have purchased this product to review it");
        }
        if (hasReviewed(productId, userId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (request.comment() == null || request.comment().isBlank()) {
            throw new IllegalArgumentException("Comment is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Review review = new Review(user, product, request.rating(), request.comment().trim(), LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public boolean canReview(Long productId, Long userId) {
        return orderRepository.existsOrderLineByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean hasReviewed(Long productId, Long userId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }
}
