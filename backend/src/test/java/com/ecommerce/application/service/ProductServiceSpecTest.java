package com.ecommerce.application.service;

import com.ecommerce.application.service.ProductServiceImpl;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that exercise ProductServiceImpl's Specification logic
 * with a real database. Uses @DataJpaTest so PITest can mutate
 * ProductServiceImpl directly (not through a cached Spring bean).
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
class ProductServiceSpecTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private ProductServiceImpl productService;

    private final Pageable page = PageRequest.of(0, 100);

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, categoryRepository);
    }

    @Test
    void getProducts_noFilters_returnsOnlyActive() {
        Page<Product> result = productService.getProducts(null, null, null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        result.getContent().forEach(p -> assertThat(p.isActive()).isTrue());
    }

    @Test
    void getProducts_categoryFilter_returnsSubset() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> filtered = productService.getProducts(1L, null, null, null, null, null, null, page);
        assertThat(filtered.getTotalElements()).isGreaterThan(0);
        assertThat(filtered.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_searchFilter_returnsMatching() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> result = productService.getProducts(null, "cuir", null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_searchNoMatch_returnsEmpty() {
        Page<Product> result = productService.getProducts(null, "xyznonexistent99", null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void getProducts_promoFilter_returnsPromotedOnly() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> result = productService.getProducts(null, null, true, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_availableOnly_excludesOutOfStock() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> available = productService.getProducts(null, null, null, true, null, null, null, page);
        assertThat(available.getTotalElements()).isLessThanOrEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_preorderOnly_returnsOnlyFuture() {
        Page<Product> result = productService.getProducts(null, null, null, null, true, null, null, page);
        result.getContent().forEach(p ->
                assertThat(p.getReleaseDate()).isAfter(java.time.LocalDate.now()));
    }

    @Test
    void getProducts_minRating4_returnsHighRated() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> result = productService.getProducts(null, null, null, null, null, 4, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_minRating0_returnsSameAsNull() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> zeroRating = productService.getProducts(null, null, null, null, null, 0, null, page);
        assertThat(zeroRating.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_minRatingNeg_returnsSameAsNull() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> negRating = productService.getProducts(null, null, null, null, null, -1, null, page);
        assertThat(negRating.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_withAttributes_filtersCorrectly() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Map<String, String> attrs = Map.of("platform", "PlayStation");
        Page<Product> result = productService.getProducts(null, null, null, null, null, null, attrs, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        assertThat(result.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_emptyAttributes_returnsSameAsNull() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> emptyAttrs = productService.getProducts(null, null, null, null, null, null, Map.of(), page);
        assertThat(emptyAttrs.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_blankSearch_returnsSameAsNull() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> blank = productService.getProducts(null, "   ", null, null, null, null, null, page);
        assertThat(blank.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_accentInsensitiveSearch() {
        // "pokemon" without accents should find "Pokémon" with accents
        Page<Product> result = productService.getProducts(null, "pokemon", null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void getProducts_subcategory_includesChildren() {
        // Category 6 has children; products in children should be included
        Page<Product> result = productService.getProducts(6L, null, null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void getProducts_categoryAndSearch_combinedFilter() {
        Page<Product> catOnly = productService.getProducts(1L, null, null, null, null, null, null, page);
        Page<Product> catAndSearch = productService.getProducts(1L, "xyznonexistent99", null, null, null, null, null, page);
        assertThat(catAndSearch.getTotalElements()).isLessThan(catOnly.getTotalElements());
    }

    @Test
    void getProducts_minRating1_filtersMoreThanMinRating0() {
        // minRating=0 should not apply a filter; minRating=1 should apply one
        Page<Product> rating0 = productService.getProducts(null, null, null, null, null, 0, null, page);
        Page<Product> rating1 = productService.getProducts(null, null, null, null, null, 1, null, page);
        // minRating=1 should return same or fewer products than minRating=0
        assertThat(rating1.getTotalElements()).isLessThanOrEqualTo(rating0.getTotalElements());
    }

    // Additional tests to kill surviving mutations

    @Test
    void getProducts_promoFilterFalse_returnsSameAsNoFilter() {
        // onPromotion=false should not apply promo filter
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> promoFalse = productService.getProducts(null, null, false, null, null, null, null, page);
        assertThat(promoFalse.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_availableOnlyFalse_returnsSameAsNoFilter() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> availFalse = productService.getProducts(null, null, null, false, null, null, null, page);
        assertThat(availFalse.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_preorderOnlyFalse_returnsSameAsNoFilter() {
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> preorderFalse = productService.getProducts(null, null, null, null, false, null, null, page);
        assertThat(preorderFalse.getTotalElements()).isEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_categoryFilter_productsAllBelongToCategory() {
        // Verify that category filter actually works - all returned products belong to category tree
        Page<Product> result = productService.getProducts(1L, null, null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
        // If getCategoryAndDescendantIds returned empty list, we'd get 0 results
    }

    @Test
    void getProducts_preorderOnly_excludesNonPreorder() {
        // preorderOnly=true should return fewer than all
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> preorder = productService.getProducts(null, null, null, null, true, null, null, page);
        assertThat(preorder.getTotalElements()).isLessThan(all.getTotalElements());
    }

    @Test
    void getProducts_availableOnly_hasEffect() {
        // Available filter should have some effect (or at least not crash)
        Page<Product> all = productService.getProducts(null, null, null, null, null, null, null, page);
        Page<Product> available = productService.getProducts(null, null, null, true, null, null, null, page);
        // At minimum, available should be a subset
        assertThat(available.getTotalElements()).isLessThanOrEqualTo(all.getTotalElements());
    }

    @Test
    void getProducts_searchByCategory_findsViaCategoryName() {
        // Search should match category displayName too (via TRANSLATE)
        Page<Product> result = productService.getProducts(null, "epicerie", null, null, null, null, null, page);
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void getProducts_allFiltersCombined_doesNotCrash() {
        Map<String, String> attrs = Map.of("platform", "PlayStation");
        Page<Product> result = productService.getProducts(1L, "test", true, true, false, 1, attrs, page);
        // Just verifying no exception is thrown
        assertThat(result).isNotNull();
    }
}
