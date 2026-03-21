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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

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
    void createOrder_validRequest_returns201() throws Exception {
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getStock() == null || p.getStock() > 0)
                .findFirst()
                .orElseThrow();

        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 1,
                        "unitPrice", product.getPrice()
                )),
                "billingStreet", "10 rue test",
                "billingCity", "Paris",
                "billingPostalCode", "75001",
                "shippingStreet", "10 rue test",
                "shippingCity", "Paris",
                "shippingPostalCode", "75001"
        );

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.lines").isArray());
    }

    @Test
    void createOrder_insufficientStock_returnsBadRequestWithStockInfo() throws Exception {
        // Find a product with limited stock
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .findFirst()
                .orElse(null);

        if (product == null) {
            // Skip if no product with limited stock exists
            return;
        }

        int requestedQty = product.getStock() + 100;

        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", requestedQty,
                        "unitPrice", product.getPrice()
                )),
                "billingStreet", "10 rue test",
                "billingCity", "Paris",
                "billingPostalCode", "75001",
                "shippingStreet", "10 rue test",
                "shippingCity", "Paris",
                "shippingPostalCode", "75001"
        );

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("INSUFFICIENT_STOCK")))
                .andExpect(jsonPath("$.productName").isNotEmpty())
                .andExpect(jsonPath("$.availableStock").isNumber())
                .andExpect(jsonPath("$.requestedQuantity", is(requestedQty)));
    }

    @Test
    void createOrder_withoutAuth_returnsForbidden() throws Exception {
        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", 1,
                        "quantity", 1,
                        "unitPrice", 10.00
                ))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrders_withAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/api/orders/999999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderById_existingOrder_returns200() throws Exception {
        // First create an order
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getStock() == null || p.getStock() > 0)
                .findFirst()
                .orElseThrow();

        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 1,
                        "unitPrice", product.getPrice()
                )),
                "billingStreet", "10 rue test",
                "billingCity", "Paris",
                "billingPostalCode", "75001",
                "shippingStreet", "10 rue test",
                "shippingCity", "Paris",
                "shippingPostalCode", "75001"
        );

        String createResponse = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(createResponse).get("id").asLong();

        // Then fetch it by ID
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.events").isArray());
    }

    @Test
    void getOrders_withLangEn_returns200() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .param("lang", "en"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_asDifferentUser_returns404() throws Exception {
        // Create an order as user001
        Product product = productRepository.findAll().stream()
                .filter(p -> p.getStock() == null || p.getStock() > 0)
                .findFirst()
                .orElseThrow();

        Map<String, Object> body = Map.of(
                "items", List.of(Map.of(
                        "productId", product.getId(),
                        "quantity", 1,
                        "unitPrice", product.getPrice()
                )),
                "billingStreet", "10 rue test",
                "billingCity", "Paris",
                "billingPostalCode", "75001",
                "shippingStreet", "10 rue test",
                "shippingCity", "Paris",
                "shippingPostalCode", "75001"
        );

        String createResponse = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(createResponse).get("id").asLong();

        // Try to access it as user002 - should return 404
        String user002Token = jwtUtil.generateToken("user002");
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + user002Token))
                .andExpect(status().isNotFound());
    }
}
