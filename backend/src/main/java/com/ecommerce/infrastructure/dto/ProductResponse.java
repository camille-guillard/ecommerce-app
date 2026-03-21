package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductTranslation;
import com.ecommerce.domain.model.Promotion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record ProductResponse(
        Long id,
        String name,
        String displayName,
        String description,
        String detailedDescription,
        BigDecimal price,
        boolean available,
        Integer stock,
        String imageUrl,
        Long categoryId,
        String categoryDisplayName,
        BigDecimal discountPercent,
        BigDecimal discountedPrice,
        Double averageRating,
        Long reviewCount,
        boolean hasVariants,
        LocalDate releaseDate,
        boolean active
) {
    public static ProductResponse fromEntity(Product product, Promotion promotion) {
        return fromEntity(product, promotion, null, null, null, null, false, product.isAvailable());
    }

    public static ProductResponse fromEntity(Product product, Promotion promotion, Double averageRating, Long reviewCount,
                                              ProductTranslation translation, String categoryDisplayNameOverride, boolean hasVariants, boolean available) {
        return fromEntity(product, promotion, averageRating, reviewCount, translation, categoryDisplayNameOverride, hasVariants, available, product.getStock());
    }

    public static ProductResponse fromEntity(Product product, Promotion promotion, Double averageRating, Long reviewCount,
                                              ProductTranslation translation, String categoryDisplayNameOverride, boolean hasVariants, boolean available, Integer effectiveStock) {
        BigDecimal discountPercent = null;
        BigDecimal discountedPrice = null;

        if (promotion != null && promotion.isActive()) {
            discountPercent = promotion.getDiscountPercent();
            discountedPrice = product.getPrice()
                    .multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        String displayName = translation != null ? translation.getDisplayName() : product.getDisplayName();
        String description = translation != null ? translation.getDescription() : product.getDescription();
        String detailedDescription = translation != null ? translation.getDetailedDescription() : null;
        String catDisplayName = categoryDisplayNameOverride != null ? categoryDisplayNameOverride : product.getCategory().getDisplayName();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                displayName,
                description,
                detailedDescription,
                product.getPrice(),
                available,
                effectiveStock,
                product.getImageUrl(),
                product.getCategory().getId(),
                catDisplayName,
                discountPercent,
                discountedPrice,
                averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null,
                reviewCount != null && reviewCount > 0 ? reviewCount : null,
                hasVariants,
                product.getReleaseDate(),
                product.isActive()
        );
    }
}
