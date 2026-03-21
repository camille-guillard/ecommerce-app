package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CategoryTranslation;

import java.util.List;
import java.util.Map;

public record CategoryResponse(
        Long id,
        String name,
        String displayName,
        Long parentId,
        List<CategoryResponse> children
) {
    public static CategoryResponse fromEntity(Category category, Map<Long, CategoryTranslation> translations) {
        CategoryTranslation ct = translations.get(category.getId());
        String display = ct != null ? ct.getDisplayName() : category.getDisplayName();
        List<CategoryResponse> childResponses = category.getChildren().stream()
                .map(child -> fromEntity(child, translations))
                .toList();
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                display,
                category.getParent() != null ? category.getParent().getId() : null,
                childResponses
        );
    }
}
