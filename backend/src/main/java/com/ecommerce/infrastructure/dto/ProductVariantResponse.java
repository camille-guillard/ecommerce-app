package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.ProductVariant;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        String color,
        String size,
        int stock,
        BigDecimal priceOverride
) {
    public static ProductVariantResponse fromEntity(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getColor(),
                variant.getSize(),
                variant.getStock(),
                variant.getPriceOverride()
        );
    }
}
