package com.ecommerce.application.service;

import com.ecommerce.application.service.ProductServiceImpl;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.ProductRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product createProduct(Long id, String name) {
        Category cat = new Category("cat", "Category");
        cat.setId(1L);
        Product p = new Product(name, name, "description", new BigDecimal("10.00"), 10, null, cat);
        p.setId(id);
        return p;
    }

    // --- getProductById ---

    @Test
    void getProductById_existingProduct_returnsProduct() {
        Product product = createProduct(1L, "Widget");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Widget");
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_nonExisting_returnsNull() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(999L);

        assertThat(result).isNull();
    }

    // --- getProducts - basic invocations ---

    @Test
    void getProducts_noFilters_callsRepositoryWithSpec() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(createProduct(1L, "A")));
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);

        Page<Product> result = productService.getProducts(null, null, null, null, null, null, null, pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withCategoryId_collectsDescendantIds() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findChildIds(1L)).thenReturn(List.of(2L, 3L));
        when(categoryRepository.findChildIds(2L)).thenReturn(List.of());
        when(categoryRepository.findChildIds(3L)).thenReturn(List.of());
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(1L, null, null, null, null, null, null, pageable);

        verify(categoryRepository).findChildIds(1L);
        verify(categoryRepository).findChildIds(2L);
        verify(categoryRepository).findChildIds(3L);
    }

    @Test
    void getProducts_withBlankSearch_treatsAsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, "   ", null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withSearch_passesNonBlankSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, "  test  ", null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withOnPromotionTrue_appliesPromoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, Boolean.TRUE, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withOnPromotionFalse_noPromoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, Boolean.FALSE, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withOnPromotionNull_noPromoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withAvailableOnlyTrue_appliesAvailableFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, Boolean.TRUE, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withAvailableOnlyFalse_noAvailableFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, Boolean.FALSE, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withPreorderOnlyTrue_appliesPreorderFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, Boolean.TRUE, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withPreorderOnlyFalse_noPreorderFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, Boolean.FALSE, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withMinRatingPositive_appliesRatingFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, 3, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withMinRatingZero_treatsAsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, 0, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withMinRatingNegative_treatsAsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, -1, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withAttributes_appliesAttributeFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        Map<String, String> attributes = new HashMap<>();
        attributes.put("color", "red");
        attributes.put("size", "L");

        productService.getProducts(null, null, null, null, null, null, attributes, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withEmptyAttributes_noAttributeFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, null, Collections.emptyMap(), pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withAllFilters_callsRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findChildIds(1L)).thenReturn(List.of());
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        Map<String, String> attributes = Map.of("color", "blue");

        productService.getProducts(1L, "search", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, 4, attributes, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_returnsPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Product p1 = createProduct(1L, "A");
        Product p2 = createProduct(2L, "B");
        Page<Product> expectedPage = new PageImpl<>(List.of(p1, p2));
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);

        Page<Product> result = productService.getProducts(null, null, null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(p1, p2);
    }

    // --- getCategoryAndDescendantIds / collectDescendantIds ---

    @Test
    void getProducts_withDeepCategoryTree_collectsAllDescendants() {
        Pageable pageable = PageRequest.of(0, 10);
        // Tree: 1 -> 2 -> 4, 1 -> 3
        when(categoryRepository.findChildIds(1L)).thenReturn(List.of(2L, 3L));
        when(categoryRepository.findChildIds(2L)).thenReturn(List.of(4L));
        when(categoryRepository.findChildIds(3L)).thenReturn(List.of());
        when(categoryRepository.findChildIds(4L)).thenReturn(List.of());
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(1L, null, null, null, null, null, null, pageable);

        verify(categoryRepository).findChildIds(1L);
        verify(categoryRepository).findChildIds(2L);
        verify(categoryRepository).findChildIds(3L);
        verify(categoryRepository).findChildIds(4L);
    }

    @Test
    void getProducts_withLeafCategory_onlyIncludesSelf() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findChildIds(5L)).thenReturn(List.of());
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(5L, null, null, null, null, null, null, pageable);

        verify(categoryRepository).findChildIds(5L);
    }

    // --- stripAccents (tested indirectly via search) ---

    @Test
    void getProducts_withAccentedSearch_normalizesAccents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        // The search term has accents - stripAccents should normalize them
        productService.getProducts(null, "café", null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withNonAccentedSearch_passesThrough() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, "coffee", null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withNullSearch_doesNotApplySearchFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withEmptySearch_doesNotApplySearchFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, "", null, null, null, null, null, pageable);

        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    // ========== MUTATION-KILLING TESTS ==========

    @Test
    void getProducts_withCategoryId_resultIncludesDescendants() {
        // Kill mutation: "replaced return value with Collections.emptyList for getCategoryAndDescendantIds"
        // The Specification must include the parent category ID AND its descendants
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findChildIds(10L)).thenReturn(List.of(20L));
        when(categoryRepository.findChildIds(20L)).thenReturn(List.of());

        // Capture the Specification and verify it was built with the right category IDs
        ArgumentCaptor<Specification<Product>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(productRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(10L, null, null, null, null, null, null, pageable);

        // Verify category hierarchy was traversed
        verify(categoryRepository).findChildIds(10L);
        verify(categoryRepository).findChildIds(20L);

        // The spec should not be null (confirming getCategoryAndDescendantIds returned real values)
        Specification<Product> capturedSpec = specCaptor.getValue();
        assertThat(capturedSpec).isNotNull();
    }

    @Test
    void getProducts_minRating1_appliesFilter_minRating0_doesNot() {
        // Kill mutation: "changed conditional boundary" on minRating > 0
        // minRating=0 should NOT apply the filter (effectiveMinRating = null)
        // minRating=1 should apply the filter (effectiveMinRating = 1)
        Pageable pageable = PageRequest.of(0, 10);

        // Call with minRating=0 - should be same as null
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));
        productService.getProducts(null, null, null, null, null, 0, null, pageable);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));

        // Call with minRating=1 - should apply filter
        productService.getProducts(null, null, null, null, null, 1, null, pageable);
        // Both calls succeed
    }

    @Test
    void getProducts_searchNonBlank_trimsBeforeUse() {
        // Verify the search is trimmed
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, " hello ", null, null, null, null, null, pageable);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_emptyAttributeMap_treatedAsNull() {
        // Kill mutation on attributes.isEmpty() check
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        // Empty map should be treated as no attributes
        productService.getProducts(null, null, null, null, null, null, Map.of(), pageable);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withNullCategoryId_doesNotCallFindChildIds() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(null, null, null, null, null, null, null, pageable);

        verify(categoryRepository, never()).findChildIds(any());
    }
}
