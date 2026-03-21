package com.ecommerce.integration;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderLine;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.CategoryRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtUtil.generateToken("admin");
        userToken = jwtUtil.generateToken("user001");
    }

    // ========== ROLE-BASED ACCESS ==========

    @Test
    void getUsers_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getUsers_asRegularUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrders_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getOrders_asRegularUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProducts_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProducts_asRegularUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCategories_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/categories")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getCategories_asRegularUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    // ========== PRODUCT CRUD ==========

    @Test
    void createProduct_asAdmin_returns201WithProduct() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Map<String, Object> body = Map.of(
                "displayName", "New Test Product",
                "description", "Test description",
                "price", 29.99,
                "stock", 50,
                "categoryId", category.getId()
        );

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName", is("New Test Product")))
                .andExpect(jsonPath("$.price", is(29.99)))
                .andExpect(jsonPath("$.stock", is(50)));
    }

    @Test
    void createProduct_asRegularUser_returns403() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Map<String, Object> body = Map.of(
                "displayName", "Unauthorized Product",
                "description", "desc",
                "price", 10.00,
                "categoryId", category.getId()
        );

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_asAdmin_updatesFields() throws Exception {
        Product product = productRepository.findAll().get(0);

        Map<String, Object> body = Map.of(
                "displayName", "Updated Name",
                "price", 99.99
        );

        mockMvc.perform(put("/api/admin/products/" + product.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Updated Name")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    void updateProduct_nonExistingProduct_returns404() throws Exception {
        Map<String, Object> body = Map.of("displayName", "X");

        mockMvc.perform(put("/api/admin/products/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    // ========== TOGGLE ACTIVE ==========

    @Test
    void toggleProductActive_asAdmin_togglesActiveField() throws Exception {
        Product product = productRepository.findAll().get(0);
        boolean originalActive = product.isActive();

        mockMvc.perform(put("/api/admin/products/" + product.getId() + "/toggle-active")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(!originalActive)));

        // Toggle back to restore state
        mockMvc.perform(put("/api/admin/products/" + product.getId() + "/toggle-active")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", is(originalActive)));
    }

    @Test
    void toggleProductActive_nonExistingProduct_returns404() throws Exception {
        mockMvc.perform(put("/api/admin/products/999999/toggle-active")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleProductActive_asRegularUser_returns403() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(put("/api/admin/products/" + product.getId() + "/toggle-active")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    // ========== ORDER STATUS TRANSITIONS ==========

    @Test
    void updateOrderStatus_confirmedToInTransit_succeeds() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_TRANSIT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_TRANSIT")));
    }

    @Test
    void updateOrderStatus_confirmedToCancelled_succeeds() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "CANCELLED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    void updateOrderStatus_inTransitToCompleted_succeeds() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.IN_TRANSIT);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "COMPLETED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    void updateOrderStatus_confirmedToCompleted_returnsBadRequest() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "COMPLETED"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_completedToAnything_returnsBadRequest() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.COMPLETED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_TRANSIT"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_cancelledToAnything_returnsBadRequest() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CANCELLED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "CONFIRMED"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_invalidStatusValue_returnsBadRequest() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "NONEXISTENT"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_missingStatusField_returnsBadRequest() throws Exception {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        Product product = productRepository.findAll().get(0);

        CustomerOrder order = new CustomerOrder(LocalDateTime.now(), BigDecimal.TEN, OrderStatus.CONFIRMED);
        order.setUser(admin);
        order.addLine(new OrderLine(order, product, 1, BigDecimal.TEN, null));
        order = orderRepository.save(order);

        mockMvc.perform(put("/api/admin/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("other", "value"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_nonExistingOrder_returns404() throws Exception {
        mockMvc.perform(put("/api/admin/orders/999999/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_TRANSIT"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_asRegularUser_returns403() throws Exception {
        mockMvc.perform(put("/api/admin/orders/1/status")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_TRANSIT"))))
                .andExpect(status().isForbidden());
    }

    // ========== CREATE PRODUCT WITH RELEASE DATE ==========

    @Test
    void createProduct_withReleaseDate_returns201() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Map<String, Object> body = new HashMap<>();
        body.put("displayName", "Future Product");
        body.put("description", "Coming soon");
        body.put("price", 49.99);
        body.put("stock", 0);
        body.put("categoryId", category.getId());
        body.put("releaseDate", "2027-06-15");

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName", is("Future Product")))
                .andExpect(jsonPath("$.releaseDate", is("2027-06-15")));
    }

    @Test
    void createProduct_withExplicitName_usesProvidedName() throws Exception {
        Category category = categoryRepository.findAll().get(0);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "custom-slug");
        body.put("displayName", "Custom Slug Product");
        body.put("description", "Has a custom name");
        body.put("price", 19.99);
        body.put("categoryId", category.getId());

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("custom-slug")));
    }

    @Test
    void createProduct_invalidCategory_returnsBadRequest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("displayName", "Bad Category Product");
        body.put("description", "desc");
        body.put("price", 10.00);
        body.put("categoryId", 999999);

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ========== UPDATE PRODUCT WITH MORE FIELDS ==========

    @Test
    void updateProduct_withAllFields_updatesAllFields() throws Exception {
        Product product = productRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);

        Map<String, Object> body = new HashMap<>();
        body.put("displayName", "Fully Updated");
        body.put("description", "New description");
        body.put("price", 199.99);
        body.put("stock", 42);
        body.put("imageUrl", "/images/updated.jpg");
        body.put("categoryId", category.getId());

        mockMvc.perform(put("/api/admin/products/" + product.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Fully Updated")))
                .andExpect(jsonPath("$.stock", is(42)))
                .andExpect(jsonPath("$.imageUrl", is("/images/updated.jpg")));
    }

    @Test
    void updateProduct_asRegularUser_returns403() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(put("/api/admin/products/" + product.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("displayName", "X"))))
                .andExpect(status().isForbidden());
    }

    // ========== UPLOAD ==========

    @Test
    void uploadImage_asAdmin_returnsUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.png", "image/png",
                new byte[]{1, 2, 3, 4, 5});

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", containsString("/images/items/")));
    }

    @Test
    void uploadImage_emptyFile_returnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.png", "image/png",
                new byte[0]);

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_asRegularUser_returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png",
                new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadImage_fileWithNoExtension_returnsUrlWithJpg() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "noextension", "image/jpeg",
                new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", containsString(".jpg")));
    }

    @Test
    void uploadImage_invalidMimeType_returnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "file.exe", "application/octet-stream",
                new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_fileWithNullFilename_returnsUrlWithJpg() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", null, "image/jpeg",
                new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/admin/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", containsString(".jpg")));
    }

    // ========== NULL AUTH ==========

    @Test
    void getUsers_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrders_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isForbidden());
    }
}
