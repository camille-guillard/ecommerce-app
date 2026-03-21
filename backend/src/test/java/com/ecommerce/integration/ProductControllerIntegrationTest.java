package com.ecommerce.integration;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getProducts_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(12)))
                .andExpect(jsonPath("$.totalElements", greaterThan(50)))
                .andExpect(jsonPath("$.totalPages", greaterThan(1)));
    }

    @Test
    void getProducts_withPagination_returnsCorrectPage() throws Exception {
        mockMvc.perform(get("/api/products").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void getProducts_filterByCategory_returnsFilteredResults() throws Exception {
        mockMvc.perform(get("/api/products").param("categoryId", "1").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    void getProducts_filterBySearch_returnsMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products").param("search", "cuir").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    void getProducts_searchByDescription_returnsMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products").param("search", "plein air").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    void getProducts_searchByCategoryName_returnsMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products").param("search", "Épicerie").param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    void getProducts_filterByCategoryAndSearch_returnsCombinedResults() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("categoryId", "4")
                        .param("search", "cuir")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }

    @Test
    void getProducts_sortByPriceAsc_returnsSortedResults() throws Exception {
        mockMvc.perform(get("/api/products").param("sort", "price,asc").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    void getProducts_sortByDisplayNameAsc_returnsSortedResults() throws Exception {
        mockMvc.perform(get("/api/products").param("sort", "displayName,asc").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    void getProductById_existingProduct_returnsProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.displayName").isNotEmpty())
                .andExpect(jsonPath("$.categoryDisplayName").isNotEmpty());
    }

    @Test
    void getProductById_nonExistingProduct_returns404() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProducts_searchWithNoMatch_returnsEmptyPage() throws Exception {
        mockMvc.perform(get("/api/products").param("search", "xyznonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    // ========== FILTER TESTS ==========

    @Test
    void getProducts_filterOnPromotion_returnsPromotedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("onPromotion", "true")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_filterAvailableOnly_returnsAvailableProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("availableOnly", "true")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_filterPreorderOnly_returnsPreorderProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("preorderOnly", "true")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_filterMinRating_returnsHighRatedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("minRating", "4")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_withAttributeFilter_returnsFilteredProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("attr_color", "Noir")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_withLangEn_returnsTranslatedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("lang", "en")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    // ========== VARIANTS ENDPOINT ==========

    @Test
    void getVariants_existingProduct_returnsVariantList() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId() + "/variants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ========== ATTRIBUTES ENDPOINT ==========

    @Test
    void getAvailableAttributes_existingCategory_returnsAttributes() throws Exception {
        mockMvc.perform(get("/api/products/attributes")
                        .param("categoryId", "1"))
                .andExpect(status().isOk());
    }

    // ========== PRODUCT BY ID WITH LANG ==========

    @Test
    void getProductById_withEnglishLang_returnsProduct() throws Exception {
        mockMvc.perform(get("/api/products/1").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
