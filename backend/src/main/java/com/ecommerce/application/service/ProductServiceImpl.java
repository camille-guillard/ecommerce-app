package com.ecommerce.application.service;

import com.ecommerce.domain.model.Product;
import java.text.Normalizer;
import com.ecommerce.domain.model.ProductAttribute;
import com.ecommerce.domain.model.ProductVariant;
import com.ecommerce.domain.model.Promotion;
import com.ecommerce.domain.model.Review;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.service.ProductService;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Product> getProducts(Long categoryId, String search, Boolean onPromotion, Boolean availableOnly, Boolean preorderOnly, Integer minRating, Map<String, String> attributes, Pageable pageable) {
        List<Long> categoryIds = categoryId != null ? getCategoryAndDescendantIds(categoryId) : null;
        String trimmedSearch = search != null && !search.isBlank() ? search.trim() : null;
        boolean hasPromoFilter = Boolean.TRUE.equals(onPromotion);
        boolean hasAvailableFilter = Boolean.TRUE.equals(availableOnly);
        boolean hasPreorderFilter = Boolean.TRUE.equals(preorderOnly);
        Integer effectiveMinRating = minRating != null && minRating > 0 ? minRating : null;

        Specification<Product> spec = buildSpec(categoryIds, trimmedSearch, hasPromoFilter, hasAvailableFilter, hasPreorderFilter, effectiveMinRating, attributes);
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    private Specification<Product> buildSpec(List<Long> categoryIds, String search, boolean promoOnly, boolean availableOnly, boolean preorderOnly, Integer minRating, Map<String, String> attributes) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("active")));

            if (minRating != null) {
                Subquery<Double> avgSubquery = query.subquery(Double.class);
                Root<Review> reviewRoot = avgSubquery.from(Review.class);
                avgSubquery.select(cb.avg(reviewRoot.get("rating")))
                        .where(cb.equal(reviewRoot.get("product").get("id"), root.get("id")));
                predicates.add(cb.greaterThanOrEqualTo(avgSubquery, minRating.doubleValue()));
            }

            if (promoOnly) {
                LocalDate today = LocalDate.now();
                Subquery<Long> promoSubquery = query.subquery(Long.class);
                Root<Promotion> promoRoot = promoSubquery.from(Promotion.class);
                promoSubquery.select(promoRoot.get("product").get("id"))
                        .where(
                                cb.lessThanOrEqualTo(promoRoot.get("startDate"), today),
                                cb.or(
                                        cb.isNull(promoRoot.get("endDate")),
                                        cb.greaterThanOrEqualTo(promoRoot.get("endDate"), today)
                                )
                        );
                predicates.add(root.get("id").in(promoSubquery));
            }

            if (availableOnly) {
                // Products with direct stock > 0, OR products with NULL stock that have at least one variant with stock > 0
                Subquery<Long> variantStockSubquery = query.subquery(Long.class);
                Root<ProductVariant> variantRoot = variantStockSubquery.from(ProductVariant.class);
                variantStockSubquery.select(variantRoot.get("product").get("id"))
                        .where(
                                cb.equal(variantRoot.get("product").get("id"), root.get("id")),
                                cb.greaterThan(variantRoot.get("stock"), 0)
                        );
                predicates.add(cb.or(
                        cb.greaterThan(root.get("stock"), 0),
                        cb.and(cb.isNull(root.get("stock")), root.get("id").in(variantStockSubquery))
                ));
            }

            if (preorderOnly) {
                predicates.add(cb.greaterThan(root.get("releaseDate"), LocalDate.now()));
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }

            if (search != null) {
                String normalizedSearch = "%" + stripAccents(search.toLowerCase()) + "%";
                String accented = "àâäéèêëïîôùûüçñÀÂÄÉÈÊËÏÎÔÙÛÜÇÑ";
                String plain    = "aaaeeeeiioouucnAAAEEEEIIOOUUCN";
                Expression<String> normalizedDisplayName = cb.function("TRANSLATE", String.class, cb.lower(root.get("displayName")), cb.literal(accented), cb.literal(plain));
                Expression<String> normalizedDescription = cb.function("TRANSLATE", String.class, cb.lower(root.get("description")), cb.literal(accented), cb.literal(plain));
                Expression<String> normalizedCategory = cb.function("TRANSLATE", String.class, cb.lower(root.get("category").get("displayName")), cb.literal(accented), cb.literal(plain));
                predicates.add(cb.or(
                        cb.like(normalizedDisplayName, normalizedSearch),
                        cb.like(normalizedDescription, normalizedSearch),
                        cb.like(normalizedCategory, normalizedSearch),
                        cb.like(cb.lower(root.get("name")), normalizedSearch)
                ));
            }

            if (attributes != null && !attributes.isEmpty()) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    Subquery<Long> attrSubquery = query.subquery(Long.class);
                    Root<ProductAttribute> attrRoot = attrSubquery.from(ProductAttribute.class);
                    attrSubquery.select(attrRoot.get("product").get("id"))
                            .where(
                                    cb.equal(attrRoot.get("attributeKey"), entry.getKey()),
                                    cb.equal(attrRoot.get("attributeValue"), entry.getValue())
                            );
                    predicates.add(root.get("id").in(attrSubquery));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Long> getCategoryAndDescendantIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        collectDescendantIds(categoryId, ids);
        return ids;
    }

    private void collectDescendantIds(Long parentId, List<Long> ids) {
        List<Long> childIds = categoryRepository.findChildIds(parentId);
        for (Long childId : childIds) {
            ids.add(childId);
            collectDescendantIds(childId, ids);
        }
    }

    private String stripAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
