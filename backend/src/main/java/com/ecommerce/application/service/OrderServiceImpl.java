package com.ecommerce.application.service;

import com.ecommerce.application.exception.InsufficientStockException;
import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderLine;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.OrderEvent;
import com.ecommerce.domain.model.ProductVariant;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.OrderEventRepository;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.model.Promotion;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.ProductVariantRepository;
import com.ecommerce.domain.repository.PromotionRepository;
import com.ecommerce.domain.service.OrderService;
import com.ecommerce.infrastructure.dto.CreateOrderRequest;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PromotionRepository promotionRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventRepository orderEventRepository,
                            ProductRepository productRepository, ProductVariantRepository productVariantRepository,
                            PromotionRepository promotionRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<CustomerOrder> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request, User user) {
        BigDecimal total = BigDecimal.ZERO;
        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.ZERO, OrderStatus.CONFIRMED);
        order.setUser(user);
        order.setBillingStreet(request.billingStreet());
        order.setBillingCity(request.billingCity());
        order.setBillingPostalCode(request.billingPostalCode());
        order.setShippingStreet(request.shippingStreet());
        order.setShippingCity(request.shippingCity());
        order.setShippingPostalCode(request.shippingPostalCode());

        for (CreateOrderRequest.OrderLineRequest item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId()));

            // Compute server-side price: ignore client-provided unitPrice
            BigDecimal unitPrice = product.getPrice();
            Promotion promo = promotionRepository.findActiveByProductId(product.getId(), LocalDate.now()).orElse(null);
            if (promo != null) {
                BigDecimal discount = promo.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                unitPrice = unitPrice.subtract(unitPrice.multiply(discount));
            }

            String variantLabel = null;
            if (item.variantId() != null) {
                ProductVariant variant = productVariantRepository.findById(item.variantId()).orElse(null);
                if (variant == null) {
                    throw new IllegalArgumentException("Variant not found: " + item.variantId());
                }
                if (item.quantity() > variant.getStock()) {
                    throw new InsufficientStockException(product.getDisplayName(), variant.getStock(), item.quantity());
                }
                variant.setStock(variant.getStock() - item.quantity());
                productVariantRepository.save(variant);
                variantLabel = Stream.of(variant.getColor(), variant.getSize())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" / "));
            } else if (product.getStock() != null) {
                if (item.quantity() > product.getStock()) {
                    throw new InsufficientStockException(product.getDisplayName(), product.getStock(), item.quantity());
                }
                product.setStock(product.getStock() - item.quantity());
                productRepository.save(product);
            }

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(lineTotal);
            order.addLine(new OrderLine(order, product, item.quantity(), unitPrice, variantLabel));
        }

        order.setTotalAmount(total);
        CustomerOrder saved = orderRepository.save(order);
        orderEventRepository.save(new OrderEvent(saved, OrderStatus.CONFIRMED));
        return saved;
    }
}
