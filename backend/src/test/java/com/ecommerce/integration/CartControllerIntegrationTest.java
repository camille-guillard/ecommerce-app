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

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerIntegrationTest {

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
    void getCart_withAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void getCart_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isForbidden());
    }

    @Test
    void syncCart_withItems_replacesCartContents() throws Exception {
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
                .andExpect(jsonPath("$.items[0].productId", is(product.getId().intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(3)));
    }

    @Test
    void syncCart_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(put("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void mergeCart_withItems_mergesWithExisting() throws Exception {
        Product p1 = productRepository.findAll().get(0);
        Product p2 = productRepository.findAll().get(1);

        // First sync a cart with p1
        Map<String, Object> syncBody = Map.of(
                "items", List.of(Map.of(
                        "productId", p1.getId(),
                        "quantity", 2
                ))
        );

        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syncBody)))
                .andExpect(status().isOk());

        // Now merge with p2
        Map<String, Object> mergeBody = Map.of(
                "items", List.of(Map.of(
                        "productId", p2.getId(),
                        "quantity", 1
                ))
        );

        mockMvc.perform(post("/api/cart/merge")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mergeBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void mergeCart_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/cart/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isForbidden());
    }
}
