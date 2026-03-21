package com.ecommerce.infrastructure.controller;

import com.ecommerce.application.exception.InsufficientStockException;
import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderEvent;
import com.ecommerce.domain.repository.OrderEventRepository;
import com.ecommerce.domain.model.ProductTranslation;
import com.ecommerce.domain.model.Review;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.ProductTranslationRepository;
import com.ecommerce.domain.repository.ReviewRepository;
import com.ecommerce.domain.service.OrderService;
import com.ecommerce.infrastructure.dto.CreateOrderRequest;
import com.ecommerce.infrastructure.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductTranslationRepository productTranslationRepository;
    private final ReviewRepository reviewRepository;
    private final OrderEventRepository orderEventRepository;

    public OrderController(OrderService orderService, ProductTranslationRepository productTranslationRepository,
                           ReviewRepository reviewRepository, OrderEventRepository orderEventRepository) {
        this.orderService = orderService;
        this.productTranslationRepository = productTranslationRepository;
        this.orderEventRepository = orderEventRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public List<OrderResponse> getOrders(Authentication authentication,
                                          @RequestParam(defaultValue = "fr") String lang) {
        User user = (User) authentication.getPrincipal();
        Map<Long, ProductTranslation> translations = getTranslationMap(lang);
        Map<Long, Integer> userRatings = getUserRatings(user.getId());
        return orderService.getOrdersByUser(user.getId()).stream()
                .map(order -> OrderResponse.fromEntity(order, translations, userRatings))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                       @RequestParam(defaultValue = "fr") String lang,
                                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CustomerOrder order = orderService.getOrderById(id);
        if (order == null || (!order.getUser().getId().equals(user.getId()) && !user.hasRole("ADMIN"))) {
            return ResponseEntity.notFound().build();
        }
        Map<Long, ProductTranslation> translations = getTranslationMap(lang);
        List<OrderEvent> events = orderEventRepository.findByOrderIdOrderByCreatedAtAsc(id);
        return ResponseEntity.ok(OrderResponse.fromEntity(order, translations, Map.of(), events));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        try {
            CustomerOrder order = orderService.createOrder(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(order));
        } catch (InsufficientStockException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "INSUFFICIENT_STOCK",
                    "message", e.getMessage(),
                    "productName", e.getProductName(),
                    "availableStock", e.getAvailableStock(),
                    "requestedQuantity", e.getRequestedQuantity()
            ));
        }
    }

    private Map<Long, ProductTranslation> getTranslationMap(String lang) {
        return productTranslationRepository.findByLocale(lang).stream()
                .collect(Collectors.toMap(t -> t.getProduct().getId(), t -> t, (a, b) -> a));
    }

    private Map<Long, Integer> getUserRatings(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Review::getRating, (a, b) -> a));
    }
}
