package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CategoryTranslation;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.CategoryTranslationRepository;
import com.ecommerce.infrastructure.dto.CategoryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;

    public CategoryController(CategoryRepository categoryRepository, CategoryTranslationRepository categoryTranslationRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryTranslationRepository = categoryTranslationRepository;
    }

    @GetMapping
    public List<CategoryResponse> getCategories(@RequestParam(defaultValue = "fr") String lang) {
        Map<Long, CategoryTranslation> translationMap = categoryTranslationRepository.findByLocale(lang).stream()
                .collect(Collectors.toMap(t -> t.getCategory().getId(), t -> t, (a, b) -> a));

        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream()
                .map(cat -> CategoryResponse.fromEntity(cat, translationMap))
                .toList();
    }
}
