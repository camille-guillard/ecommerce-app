package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderEvent;
import com.ecommerce.domain.model.OrderLine;
import com.ecommerce.domain.model.ProductTranslation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OrderResponse(
        Long id,
        LocalDateTime createdAt,
        BigDecimal totalAmount,
        String status,
        String username,
        String billingStreet,
        String billingCity,
        String billingPostalCode,
        String shippingStreet,
        String shippingCity,
        String shippingPostalCode,
        List<OrderLineResponse> lines,
        List<OrderEventResponse> events
) {
    public static OrderResponse fromEntity(CustomerOrder order) {
        return fromEntity(order, Map.of(), Map.of(), List.of());
    }

    public static OrderResponse fromEntity(CustomerOrder order, Map<Long, ProductTranslation> translations) {
        return fromEntity(order, translations, Map.of(), List.of());
    }

    public static OrderResponse fromEntity(CustomerOrder order, Map<Long, ProductTranslation> translations,
                                            Map<Long, Integer> userRatings) {
        return fromEntity(order, translations, userRatings, List.of());
    }

    public static OrderResponse fromEntity(CustomerOrder order, Map<Long, ProductTranslation> translations,
                                            Map<Long, Integer> userRatings, List<OrderEvent> events) {
        String username = order.getUser() != null ? order.getUser().getUsername() : null;
        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getStatus().name(),
                username,
                order.getBillingStreet(),
                order.getBillingCity(),
                order.getBillingPostalCode(),
                order.getShippingStreet(),
                order.getShippingCity(),
                order.getShippingPostalCode(),
                order.getLines().stream().map(line -> OrderLineResponse.fromEntity(line, translations, userRatings)).toList(),
                events.stream().map(OrderEventResponse::fromEntity).toList()
        );
    }

    public record OrderLineResponse(
            Long productId,
            String productDisplayName,
            String productImageUrl,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal,
            Integer userRating,
            String variantLabel
    ) {
        public static OrderLineResponse fromEntity(OrderLine line, Map<Long, ProductTranslation> translations,
                                                    Map<Long, Integer> userRatings) {
            ProductTranslation pt = translations.get(line.getProduct().getId());
            String displayName = pt != null ? pt.getDisplayName() : line.getProduct().getDisplayName();
            return new OrderLineResponse(
                    line.getProduct().getId(),
                    displayName,
                    line.getProduct().getImageUrl(),
                    line.getQuantity(),
                    line.getUnitPrice(),
                    line.getSubtotal(),
                    userRatings.get(line.getProduct().getId()),
                    line.getVariantLabel()
            );
        }
    }

    public record OrderEventResponse(String status, LocalDateTime createdAt) {
        public static OrderEventResponse fromEntity(OrderEvent event) {
            return new OrderEventResponse(event.getStatus().name(), event.getCreatedAt());
        }
    }
}
