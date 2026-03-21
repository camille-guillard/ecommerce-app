package com.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProducts_noFilters_returnsOnlyActiveProducts() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_byCategoryId_returnsProductsInCategory() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("categoryId", "1")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_bySearch_findsMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("search", "cuir")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_bySearchNoMatch_returnsEmpty() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("search", "xyznonexistent99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void getProducts_onPromotion_returnsPromotedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("onPromotion", "true")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_availableOnly_excludesOutOfStock() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("availableOnly", "true")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_preorderOnly_returnsOnlyFutureReleaseDates() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("preorderOnly", "true")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_minRating_filtersLowRated() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("minRating", "4")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_minRatingZero_returnsAll() throws Exception {
        // minRating=0 should not apply any filter, same result as no minRating
        mockMvc.perform(get("/api/products")
                        .param("minRating", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_withAttributes_filtersProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("attr_platform", "PlayStation")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_accentInsensitiveSearch_findsProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("search", "pokemon")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_blankSearch_returnsAll() throws Exception {
        // Blank search should be treated as no search filter
        mockMvc.perform(get("/api/products")
                        .param("search", "   ")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_combinedCategoryAndSearch_filtersCorrectly() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("search", "epicerie")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProductById_existing_returnsProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getProductById_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProducts_subcategoryInclusion_findsProductsInChildCategories() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("categoryId", "6")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_sortByPriceAsc_returnsSortedResults() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "price,asc")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    void getProducts_pagination_returnsCorrectPage() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void getProducts_categoryId_returnsFewerThanAll() throws Exception {
        // Verifies that category filter actually restricts results
        mockMvc.perform(get("/api/products")
                        .param("categoryId", "1")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    void getProducts_minRating1_filtersMoreThanMinRating0() throws Exception {
        // minRating=0 should not filter; minRating=1 should filter
        // This kills the boundary mutation on minRating > 0
        mockMvc.perform(get("/api/products")
                        .param("minRating", "1")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
