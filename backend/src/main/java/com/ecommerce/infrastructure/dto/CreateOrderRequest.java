package com.ecommerce.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        List<OrderLineRequest> items,
        String billingStreet,
        String billingCity,
        String billingPostalCode,
        String shippingStreet,
        String shippingCity,
        String shippingPostalCode
) {
    public record OrderLineRequest(Long productId, int quantity, BigDecimal unitPrice, Long variantId) {
    }
}
