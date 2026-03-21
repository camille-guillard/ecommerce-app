package com.ecommerce.integration;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.infrastructure.config.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartPersistenceServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepository productRepository;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtUtil.generateToken("user001");
    }

    @Test
    void getCart_createsCartIfNotExists() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void syncCart_replacesCartContents() throws Exception {
        Product product = productRepository.findAll().get(0);

        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 3
                ))
        );

        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity", is(3)));
    }

    @Test
    void syncCart_emptyItems_clearsCart() throws Exception {
        Product product = productRepository.findAll().get(0);

        // First add something
        Map<String, Object> addBody = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 2
                ))
        );
        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBody)))
                .andExpect(status().isOk());

        // Then sync with empty
        Map<String, Object> emptyBody = Map.of("items", List.of());
        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void mergeCart_addsQuantitiesForExistingProducts() throws Exception {
        Product product = productRepository.findAll().get(0);

        // Set initial cart
        Map<String, Object> syncBody = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 2
                ))
        );
        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syncBody)))
                .andExpect(status().isOk());

        // Merge with additional quantities
        Map<String, Object> mergeBody = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 3
                ))
        );
        mockMvc.perform(post("/api/cart/merge")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity", is(5)));
    }

    @Test
    void mergeCart_addsNewProducts() throws Exception {
        Product p1 = productRepository.findAll().get(0);
        Product p2 = productRepository.findAll().get(1);

        // Set initial cart with p1
        Map<String, Object> syncBody = Map.of(
                "items", List.of(Map.of(
                        "productId", p1.getId(),
                        "quantity", 1
                ))
        );
        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syncBody)))
                .andExpect(status().isOk());

        // Merge with p2
        Map<String, Object> mergeBody = Map.of(
                "items", List.of(Map.of(
                        "productId", p2.getId(),
                        "quantity", 2
                ))
        );
        mockMvc.perform(post("/api/cart/merge")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)));
    }
}
