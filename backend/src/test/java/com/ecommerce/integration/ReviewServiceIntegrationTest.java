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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtUtil.generateToken("user001");
    }

    @Test
    void canReview_userWithOrderContainingProduct_returnsTrue() throws Exception {
        User user = userRepository.findByUsername("user001").orElseThrow();
        Product product = productRepository.findAll().get(0);

        // Create an order for user001 with this product
        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(user);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        orderRepository.save(order);

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews/can-review")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReview", is(true)));
    }

    @Test
    void canReview_userWithoutOrder_returnsFalse() throws Exception {
        // Use a product that user001 has never ordered (find a product with high ID)
        Product product = productRepository.findAll().stream()
                .skip(productRepository.findAll().size() - 1)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/products/" + product.getId() + "/reviews/can-review")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canReview", is(false)));
    }

    @Test
    void createReview_validRequest_savesReview() throws Exception {
        User user = userRepository.findByUsername("user001").orElseThrow();
        Product product = productRepository.findAll().get(25);

        // Create an order for this user and product
        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(user);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        orderRepository.save(order);

        Map<String, Object> body = Map.of(
                "rating", 5,
                "comment", "Excellent product!"
        );

        mockMvc.perform(post("/api/products/" + product.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("Excellent product!")));
    }

    @Test
    void createReview_userHasNotOrdered_returnsBadRequest() throws Exception {
        Product product = productRepository.findAll().stream()
                .skip(productRepository.findAll().size() - 1)
                .findFirst()
                .orElseThrow();

        Map<String, Object> body = Map.of(
                "rating", 4,
                "comment", "text"
        );

        mockMvc.perform(post("/api/products/" + product.getId() + "/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
