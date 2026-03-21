package com.ecommerce.integration;

import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderLine;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.infrastructure.config.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtUtil.generateToken("user001");
    }

    @Test
    void getReviews_existingProduct_returns200() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getReviewSummary_existingProduct_returns200() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").isNumber())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.histogram").isMap());
    }

    @Test
    void canReview_withAuth_returnsCanReviewResponse() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews/can-review")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReview").isBoolean())
                .andExpect(jsonPath("$.hasReviewed").isBoolean());
    }

    @Test
    void canReview_withoutAuth_returnsFalseDefaults() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews/can-review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReview", is(false)))
                .andExpect(jsonPath("$.hasReviewed", is(false)));
    }

    @Test
    void createReview_withoutAuth_returnsForbidden() throws Exception {
        Product product = productRepository.findAll().get(0);

        Map<String, Object> body = Map.of(
                "rating", 4,
                "comment", "Great product!"
        );

        mockMvc.perform(post("/api/products/" + product.getId() + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createReview_validRequest_returns201() throws Exception {
        User user = userRepository.findByUsername("user001").orElseThrow();
        // Use a product far enough in the list to avoid collisions with other tests
        Product product = productRepository.findAll().get(20);

        // Ensure the user has an order containing this product
        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(user);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        orderRepository.save(order);

        Map<String, Object> body = Map.of(
                "rating", 5,
                "comment", "Excellent product for integration test!"
        );

        mockMvc.perform(post("/api/products/" + product.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("Excellent product for integration test!")))
                .andExpect(jsonPath("$.username", is("user001")));
    }

    @Test
    void createReview_invalidRating_returnsBadRequest() throws Exception {
        User user = userRepository.findByUsername("user001").orElseThrow();
        Product product = productRepository.findAll().get(21);

        // Ensure the user has an order containing this product
        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(user);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        orderRepository.save(order);

        Map<String, Object> body = Map.of(
                "rating", 0,
                "comment", "Bad rating"
        );

        mockMvc.perform(post("/api/products/" + product.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
