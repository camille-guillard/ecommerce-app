package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.CategoryTranslation;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductTranslation;
import com.ecommerce.domain.model.ProductVariant;
import com.ecommerce.domain.model.Promotion;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.CategoryTranslationRepository;
import com.ecommerce.domain.repository.ProductAttributeRepository;
import com.ecommerce.domain.repository.ProductVariantRepository;
import com.ecommerce.domain.repository.ProductTranslationRepository;
import com.ecommerce.domain.repository.PromotionRepository;
import com.ecommerce.domain.repository.ReviewRepository;
import com.ecommerce.domain.service.ProductService;
import com.ecommerce.infrastructure.dto.ProductResponse;
import com.ecommerce.infrastructure.dto.ProductVariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PromotionRepository promotionRepository;
    private final ReviewRepository reviewRepository;
    private final ProductTranslationRepository productTranslationRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, PromotionRepository promotionRepository,
                             ReviewRepository reviewRepository, ProductTranslationRepository productTranslationRepository,
                             CategoryTranslationRepository categoryTranslationRepository,
                             ProductAttributeRepository productAttributeRepository,
                             ProductVariantRepository productVariantRepository,
                             CategoryRepository categoryRepository) {
        this.productService = productService;
        this.promotionRepository = promotionRepository;
        this.reviewRepository = reviewRepository;
        this.productTranslationRepository = productTranslationRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productVariantRepository = productVariantRepository;
        this.categoryRepository = categoryRepository;
        this.categoryTranslationRepository = categoryTranslationRepository;
    }

    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean onPromotion,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) Boolean preorderOnly,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "fr") String lang,
            @RequestParam Map<String, String> allParams,
            @PageableDefault(size = 12) Pageable pageable) {

        Map<String, String> attributes = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("attr_") && !value.isEmpty()) {
                attributes.put(key.substring(5), value);
            }
        });

        Page<Product> products = productService.getProducts(categoryId, search, onPromotion, availableOnly, preorderOnly, minRating, attributes.isEmpty() ? null : attributes, pageable);
        List<Promotion> activePromos = promotionRepository.findAllActive(LocalDate.now());
        Map<Long, Promotion> promoByProductId = activePromos.stream()
                .collect(Collectors.toMap(p -> p.getProduct().getId(), p -> p, (a, b) -> a));

        Map<Long, ProductTranslation> translationMap = productTranslationRepository.findByLocale(lang).stream()
                .collect(Collectors.toMap(t -> t.getProduct().getId(), t -> t, (a, b) -> a));

        Map<Long, CategoryTranslation> catTranslationMap = categoryTranslationRepository.findByLocale(lang).stream()
                .collect(Collectors.toMap(t -> t.getCategory().getId(), t -> t, (a, b) -> a));

        List<Long> productIds = products.getContent().stream().map(Product::getId).toList();

        Map<Long, Double> avgRatings = new HashMap<>();
        Map<Long, Long> reviewCounts = new HashMap<>();
        if (!productIds.isEmpty()) {
            reviewRepository.findAverageRatingsAndCountsByProductIds(productIds).forEach(row -> {
                avgRatings.put((Long) row[0], (Double) row[1]);
                reviewCounts.put((Long) row[0], (Long) row[2]);
            });
        }

        Map<Long, List<ProductVariant>> variantsByProductId = productVariantRepository.findByProductIdIn(productIds)
                .stream().collect(Collectors.groupingBy(v -> v.getProduct().getId()));

        return products.map(product -> {
            Double avg = avgRatings.get(product.getId());
            Long count = reviewCounts.get(product.getId());
            ProductTranslation pt = translationMap.get(product.getId());
            CategoryTranslation ct = catTranslationMap.get(product.getCategory().getId());
            List<ProductVariant> variants = variantsByProductId.getOrDefault(product.getId(), List.of());
            boolean hasVariants = !variants.isEmpty();
            boolean available;
            Integer effectiveStock = product.getStock();
            if (hasVariants) {
                int totalVariantStock = variants.stream().mapToInt(ProductVariant::getStock).sum();
                available = totalVariantStock > 0;
                effectiveStock = totalVariantStock;
            } else {
                available = product.isAvailable();
            }
            return ProductResponse.fromEntity(product, promoByProductId.get(product.getId()), avg, count,
                    pt, ct != null ? ct.getDisplayName() : null, hasVariants, available, effectiveStock);
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "fr") String lang) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        Promotion promo = promotionRepository.findActiveByProductId(id, LocalDate.now()).orElse(null);
        Double avg = reviewRepository.findAverageRatingByProductId(id);
        Long count = reviewRepository.countByProductId(id);
        ProductTranslation pt = productTranslationRepository.findByProductIdAndLocale(id, lang).orElse(null);
        CategoryTranslation ct = categoryTranslationRepository.findByCategoryIdAndLocale(product.getCategory().getId(), lang).orElse(null);
        boolean hasVariants = productVariantRepository.existsByProductId(id);
        boolean available;
        Integer effectiveStock = product.getStock();
        if (hasVariants) {
            var variants = productVariantRepository.findByProductId(id);
            int totalVariantStock = variants.stream().mapToInt(ProductVariant::getStock).sum();
            available = totalVariantStock > 0;
            effectiveStock = totalVariantStock;
        } else {
            available = product.isAvailable();
        }
        return ResponseEntity.ok(ProductResponse.fromEntity(product, promo, avg, count,
                pt, ct != null ? ct.getDisplayName() : null, hasVariants, available, effectiveStock));
    }

    @GetMapping("/{id}/variants")
    public List<ProductVariantResponse> getVariants(@PathVariable Long id) {
        return productVariantRepository.findByProductId(id).stream()
                .map(ProductVariantResponse::fromEntity)
                .toList();
    }

    @GetMapping("/attributes")
    public Map<String, List<String>> getAvailableAttributes(@RequestParam Long categoryId) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        collectChildIds(categoryId, categoryIds);

        List<Object[]> rows = productAttributeRepository.findDistinctAttributesByCategoryIds(categoryIds);
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = (String) row[0];
            String value = (String) row[1];
            result.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return result;
    }

    private void collectChildIds(Long parentId, List<Long> ids) {
        List<Long> childIds = categoryRepository.findChildIds(parentId);
        for (Long childId : childIds) {
            ids.add(childId);
            collectChildIds(childId, ids);
        }
    }
}
