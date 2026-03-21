package com.ecommerce.integration;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.domain.repository.WishlistRepository;
import com.ecommerce.infrastructure.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishlistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtUtil.generateToken("user001");
    }

    @Test
    void getWishlist_withAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getWishlist_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/wishlist"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addToWishlist_validProduct_returns200() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(post("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void addToWishlist_duplicateProduct_returns200Idempotent() throws Exception {
        Product product = productRepository.findAll().get(1);

        // Add once
        mockMvc.perform(post("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Add again - should be idempotent
        mockMvc.perform(post("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void addToWishlist_nonExistingProduct_returns404() throws Exception {
        mockMvc.perform(post("/api/wishlist/999999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeFromWishlist_existingItem_returns200() throws Exception {
        Product product = productRepository.findAll().get(2);

        // Add first
        mockMvc.perform(post("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Remove
        mockMvc.perform(delete("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void removeFromWishlist_nonExistingItem_returns200() throws Exception {
        mockMvc.perform(delete("/api/wishlist/999999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void addAndListWishlist_returnsAddedProduct() throws Exception {
        Product product = productRepository.findAll().get(3);

        mockMvc.perform(post("/api/wishlist/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void addToWishlist_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/wishlist/1"))
                .andExpect(status().isForbidden());
    }
}
