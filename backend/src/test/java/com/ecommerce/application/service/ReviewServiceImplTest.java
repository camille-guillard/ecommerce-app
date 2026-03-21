package com.ecommerce.application.service;

import com.ecommerce.application.service.ReviewServiceImpl;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderLine;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.Review;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.ReviewRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.infrastructure.dto.CreateReviewRequest;
import com.ecommerce.infrastructure.dto.ReviewSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User createUser(Long id) {
        User user = new User("user" + id, "hash", "user" + id + "@test.com", "First", "Last", null, null, null);
        user.setId(id);
        return user;
    }

    private Product createProduct(Long id) {
        Category cat = new Category("cat", "Category");
        cat.setId(1L);
        Product p = new Product("product-" + id, "Product " + id, "desc", new BigDecimal("10.00"), 10, null, cat);
        p.setId(id);
        return p;
    }

    @Test
    void canReview_userHasOrderedProduct_returnsTrue() {
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);

        assertThat(reviewService.canReview(1L, 1L)).isTrue();
    }

    @Test
    void canReview_userHasNotOrderedProduct_returnsFalse() {
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThat(reviewService.canReview(1L, 1L)).isFalse();
    }

    @Test
    void canReview_noOrders_returnsFalse() {
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(false);
        assertThat(reviewService.canReview(1L, 1L)).isFalse();
    }

    @Test
    void createReview_validRequest_savesAndReturnsReview() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        OrderLine line = new OrderLine(order, product, 1, BigDecimal.TEN, null);
        order.addLine(line);

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        Review result = reviewService.createReview(1L, 1L, new CreateReviewRequest(4, "Great product!"));

        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Great product!");
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getProduct()).isEqualTo(product);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_userHasNotOrdered_throwsException() {
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(4, "text")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_userAlreadyReviewed_throwsException() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(4, "text")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_ratingTooLow_throwsException() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(0, "text")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_ratingTooHigh_throwsException() {
        Product product = createProduct(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(6, "text")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_emptyComment_throwsException() {
        Product product = createProduct(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(3, "")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_nullComment_throwsException() {
        Product product = createProduct(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(3, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getReviewSummary_withReviews_returnsCorrectSummary() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.5);
        when(reviewRepository.countByProductId(1L)).thenReturn(10L);
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(List.of(
                new Object[]{5, 6L},
                new Object[]{4, 3L},
                new Object[]{3, 1L}
        ));

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        assertThat(summary.averageRating()).isEqualTo(4.5);
        assertThat(summary.count()).isEqualTo(10L);
        assertThat(summary.histogram()).containsEntry(5, 6L);
        assertThat(summary.histogram()).containsEntry(4, 3L);
        assertThat(summary.histogram()).containsEntry(3, 1L);
        assertThat(summary.histogram()).containsEntry(2, 0L);
        assertThat(summary.histogram()).containsEntry(1, 0L);
    }

    @Test
    void getReviewSummary_noReviews_returnsZeroDefaults() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(null);
        when(reviewRepository.countByProductId(1L)).thenReturn(null);
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(List.of());

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        assertThat(summary.averageRating()).isEqualTo(0.0);
        assertThat(summary.count()).isEqualTo(0L);
        assertThat(summary.histogram().values()).containsOnly(0L);
    }

    @Test
    void hasReviewed_delegatesToRepository() {
        when(reviewRepository.existsByUserIdAndProductId(1L, 2L)).thenReturn(true);
        assertThat(reviewService.hasReviewed(2L, 1L)).isTrue();
    }

    @Test
    void getReviewsByProductId_delegatesToRepository() {
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        List<Review> result = reviewService.getReviewsByProductId(1L);
        assertThat(result).isEmpty();
        verify(reviewRepository).findByProductIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getReviewsByProductId_returnsReviews() {
        User user = createUser(1L);
        Product product = createProduct(1L);
        Review review = new Review(user, product, 5, "Excellent", LocalDateTime.now());
        review.setId(1L);
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(review));

        List<Review> result = reviewService.getReviewsByProductId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(5);
        assertThat(result.get(0).getComment()).isEqualTo("Excellent");
    }

    @Test
    void createReview_returnsSavedReview() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(42L);
            return r;
        });

        Review result = reviewService.createReview(1L, 1L, new CreateReviewRequest(5, "Amazing"));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Amazing");
    }

    @Test
    void createReview_trimsComment() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(1L, 1L, new CreateReviewRequest(3, "  trimmed comment  "));

        assertThat(result.getComment()).isEqualTo("trimmed comment");
    }

    @Test
    void getReviewSummary_returnsCorrectHistogram() {
        when(reviewRepository.findAverageRatingByProductId(2L)).thenReturn(3.0);
        when(reviewRepository.countByProductId(2L)).thenReturn(5L);
        when(reviewRepository.findRatingHistogramByProductId(2L)).thenReturn(List.of(
                new Object[]{1, 1L},
                new Object[]{2, 1L},
                new Object[]{3, 1L},
                new Object[]{4, 1L},
                new Object[]{5, 1L}
        ));

        ReviewSummaryResponse summary = reviewService.getReviewSummary(2L);

        assertThat(summary.averageRating()).isEqualTo(3.0);
        assertThat(summary.count()).isEqualTo(5L);
        assertThat(summary.histogram()).hasSize(5);
        assertThat(summary.histogram().get(1)).isEqualTo(1L);
        assertThat(summary.histogram().get(5)).isEqualTo(1L);
    }

    @Test
    void createReview_validBoundaryRating1_succeeds() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(1L, 1L, new CreateReviewRequest(1, "Minimum rating"));

        assertThat(result.getRating()).isEqualTo(1);
    }

    @Test
    void createReview_validBoundaryRating5_succeeds() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(1L, 1L, new CreateReviewRequest(5, "Maximum rating"));

        assertThat(result.getRating()).isEqualTo(5);
    }

    @Test
    void createReview_userNotFound_throwsException() {
        Product product = createProduct(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(3, "Good")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createReview_productNotFound_throwsException() {
        Product product = createProduct(1L);
        User user = createUser(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(3, "Good")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ========== MUTATION-KILLING BOUNDARY TESTS ==========

    @Test
    void canReview_matchesExactProductId() {
        // Verifies that product ID comparison uses .equals() correctly
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 2L)).thenReturn(true);
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(false);

        // Can review product 2 (ordered), but NOT product 1 (not ordered)
        assertThat(reviewService.canReview(2L, 1L)).isTrue();
        assertThat(reviewService.canReview(1L, 1L)).isFalse();
    }

    @Test
    void hasReviewed_returnsFalse_whenNotReviewed() {
        when(reviewRepository.existsByUserIdAndProductId(1L, 2L)).thenReturn(false);
        assertThat(reviewService.hasReviewed(2L, 1L)).isFalse();
    }

    @Test
    void getReviewSummary_roundsAverageToOneDecimal() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.56);
        when(reviewRepository.countByProductId(1L)).thenReturn(3L);
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(List.of());

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        // 4.56 -> Math.round(45.6) / 10.0 = 46 / 10.0 = 4.6
        assertThat(summary.averageRating()).isEqualTo(4.6);
    }

    @Test
    void getReviewSummary_countNull_returnsZero() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.0);
        when(reviewRepository.countByProductId(1L)).thenReturn(null);
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(List.of());

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        assertThat(summary.count()).isEqualTo(0L);
    }

    @Test
    void getReviewSummary_avgNull_returnsZero() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(null);
        when(reviewRepository.countByProductId(1L)).thenReturn(5L);
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(List.of());

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        assertThat(summary.averageRating()).isEqualTo(0.0);
        assertThat(summary.count()).isEqualTo(5L);
    }

    @Test
    void createReview_blankComment_throwsException() {
        Product product = createProduct(1L);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));

        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, new CreateReviewRequest(3, "   ")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void canReview_multipleOrdersMultipleLines_findsMatch() {
        when(orderRepository.existsOrderLineByUserIdAndProductId(1L, 5L)).thenReturn(true);

        assertThat(reviewService.canReview(5L, 1L)).isTrue();
    }

    @Test
    void getReviewSummary_histogramMergesCorrectly() {
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(3.0);
        when(reviewRepository.countByProductId(1L)).thenReturn(2L);
        List<Object[]> histogram = new ArrayList<>();
        histogram.add(new Object[]{3, 2L});
        when(reviewRepository.findRatingHistogramByProductId(1L)).thenReturn(histogram);

        ReviewSummaryResponse summary = reviewService.getReviewSummary(1L);

        // Only rating 3 has data; rest should be 0
        assertThat(summary.histogram().get(1)).isEqualTo(0L);
        assertThat(summary.histogram().get(2)).isEqualTo(0L);
        assertThat(summary.histogram().get(3)).isEqualTo(2L);
        assertThat(summary.histogram().get(4)).isEqualTo(0L);
        assertThat(summary.histogram().get(5)).isEqualTo(0L);
    }
}
