package com.ecommerce.application.service;

import com.ecommerce.application.exception.InsufficientStockException;
import com.ecommerce.application.service.OrderServiceImpl;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.OrderEvent;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductVariant;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.OrderEventRepository;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.ProductVariantRepository;
import com.ecommerce.domain.repository.PromotionRepository;
import com.ecommerce.infrastructure.dto.CreateOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderEventRepository orderEventRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        lenient().when(promotionRepository.findActiveByProductId(any(), any(LocalDate.class)))
                .thenReturn(Optional.empty());
    }

    private User createUser() {
        User user = new User("testuser", "hash", "test@test.com", "Test", "User", "street", "city", "00000");
        user.setId(1L);
        return user;
    }

    private Product createProduct(Long id, String name, BigDecimal price, Integer stock) {
        Category cat = new Category("cat", "Category");
        cat.setId(1L);
        Product p = new Product(name, name, "desc", price, stock, null, cat);
        p.setId(id);
        return p;
    }

    @Test
    void createOrder_singleItem_calculatesTotalAndSavesOrder() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), 100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> {
            CustomerOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 3, new BigDecimal("10.00"), null)),
                "billing st", "billing city", "12345",
                "shipping st", "shipping city", "67890"
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.getLines()).hasSize(1);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getBillingStreet()).isEqualTo("billing st");
        assertThat(result.getShippingCity()).isEqualTo("shipping city");
        assertThat(product.getStock()).isEqualTo(97);
        verify(productRepository).save(product);
    }

    @Test
    void createOrder_multipleItems_calculatesTotalCorrectly() {
        User user = createUser();
        Product p1 = createProduct(1L, "A", new BigDecimal("5.00"), 10);
        Product p2 = createProduct(2L, "B", new BigDecimal("20.00"), 50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderRequest.OrderLineRequest(1L, 2, new BigDecimal("5.00"), null),
                        new CreateOrderRequest.OrderLineRequest(2L, 1, new BigDecimal("20.00"), null)
                ),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(result.getLines()).hasSize(2);
    }

    @Test
    void createOrder_insufficientStock_throwsInsufficientStockException() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 5, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(InsufficientStockException.class)
                .satisfies(ex -> {
                    InsufficientStockException ise = (InsufficientStockException) ex;
                    assertThat(ise.getProductName()).isEqualTo("Widget");
                    assertThat(ise.getAvailableStock()).isEqualTo(2);
                    assertThat(ise.getRequestedQuantity()).isEqualTo(5);
                });
    }

    @Test
    void createOrder_withVariantId_checksVariantStock() {
        User user = createUser();
        Product product = createProduct(1L, "VariantProduct", new BigDecimal("10.00"), null);

        ProductVariant v1 = new ProductVariant();
        v1.setId(10L);
        v1.setStock(5);
        v1.setProduct(product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(v1));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 3, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines()).hasSize(1);
        assertThat(v1.getStock()).isEqualTo(2);
        verify(productRepository, never()).save(product);
    }

    @Test
    void createOrder_withVariantId_insufficientStock_throws() {
        User user = createUser();
        Product product = createProduct(1L, "VariantProduct", new BigDecimal("10.00"), null);

        ProductVariant v1 = new ProductVariant();
        v1.setId(10L);
        v1.setStock(2);
        v1.setProduct(product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(v1));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 5, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void createOrder_productNotFound_throwsIllegalArgument() {
        User user = createUser();
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(999L, 1, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void createOrder_stockDecrementedExactlyToZero_succeeds() {
        User user = createUser();
        Product product = createProduct(1L, "LastOne", new BigDecimal("10.00"), 3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 3, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(product.getStock()).isEqualTo(0);
        assertThat(result.getLines()).hasSize(1);
    }

    @Test
    void getOrdersByUser_delegatesToRepository() {
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        List<CustomerOrder> result = orderService.getOrdersByUser(1L);
        assertThat(result).isEmpty();
        verify(orderRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getOrderById_existingOrder_returnsOrder() {
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        CustomerOrder result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getOrderById_nonExisting_returnsNull() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThat(orderService.getOrderById(999L)).isNull();
    }

    @Test
    void createOrder_withVariant_setsVariantLabelFromColorAndSize() {
        User user = createUser();
        Product product = createProduct(1L, "ShoeProduct", new BigDecimal("50.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setStock(20);
        variant.setProduct(product);
        variant.setColor("Red");
        variant.setSize("42");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("50.00"), 10L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines()).hasSize(1);
        assertThat(result.getLines().get(0).getVariantLabel()).isEqualTo("Red / 42");
    }

    @Test
    void createOrder_withVariantColorOnly_setsVariantLabelWithColorOnly() {
        User user = createUser();
        Product product = createProduct(1L, "ColorOnly", new BigDecimal("30.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(11L);
        variant.setStock(10);
        variant.setProduct(product);
        variant.setColor("Blue");
        variant.setSize(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(11L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("30.00"), 11L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines().get(0).getVariantLabel()).isEqualTo("Blue");
    }

    @Test
    void createOrder_withVariantSizeOnly_setsVariantLabelWithSizeOnly() {
        User user = createUser();
        Product product = createProduct(1L, "SizeOnly", new BigDecimal("20.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(12L);
        variant.setStock(10);
        variant.setProduct(product);
        variant.setColor(null);
        variant.setSize("L");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(12L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("20.00"), 12L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines().get(0).getVariantLabel()).isEqualTo("L");
    }

    @Test
    void createOrder_savesOrderEvent() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), 100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> {
            CustomerOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        orderService.createOrder(request, user);

        ArgumentCaptor<OrderEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderEvent.class);
        verify(orderEventRepository).save(eventCaptor.capture());
        OrderEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedEvent.getOrder()).isNotNull();
    }

    @Test
    void createOrder_variantNotFound_throwsIllegalArgument() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(99L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("10.00"), 99L)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Variant not found");
    }

    @Test
    void createOrder_variantStockDeducted_savesVariant() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setStock(10);
        variant.setProduct(product);
        variant.setColor("Green");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 3, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        orderService.createOrder(request, user);

        assertThat(variant.getStock()).isEqualTo(7);
        verify(productVariantRepository).save(variant);
    }

    @Test
    void createOrder_nullStock_doesNotDeductStock() {
        User user = createUser();
        Product product = createProduct(1L, "Digital", new BigDecimal("10.00"), null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(product.getStock()).isNull();
        assertThat(result.getLines()).hasSize(1);
        verify(productRepository, never()).save(product);
    }

    @Test
    void getOrdersByUser_withOrders_returnsOrderList() {
        CustomerOrder o1 = new CustomerOrder();
        o1.setId(1L);
        CustomerOrder o2 = new CustomerOrder();
        o2.setId(2L);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(o1, o2));

        List<CustomerOrder> result = orderService.getOrdersByUser(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    // ========== MUTATION-KILLING TESTS ==========

    @Test
    void createOrder_singleItem_setsBillingAndShippingFields() {
        User user = createUser();
        Product product = createProduct(1L, "Widget", new BigDecimal("10.00"), 100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("10.00"), null)),
                "billing st", "billing city", "12345",
                "shipping st", "shipping city", "67890"
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getBillingStreet()).isEqualTo("billing st");
        assertThat(result.getBillingCity()).isEqualTo("billing city");
        assertThat(result.getBillingPostalCode()).isEqualTo("12345");
        assertThat(result.getShippingStreet()).isEqualTo("shipping st");
        assertThat(result.getShippingCity()).isEqualTo("shipping city");
        assertThat(result.getShippingPostalCode()).isEqualTo("67890");
    }

    @Test
    void createOrder_totalIsExactSumOfLines() {
        User user = createUser();
        Product p1 = createProduct(1L, "A", new BigDecimal("7.50"), 10);
        Product p2 = createProduct(2L, "B", new BigDecimal("12.30"), 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderRequest.OrderLineRequest(1L, 2, new BigDecimal("7.50"), null),
                        new CreateOrderRequest.OrderLineRequest(2L, 3, new BigDecimal("12.30"), null)
                ),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        // 7.50 * 2 = 15.00, 12.30 * 3 = 36.90, total = 51.90
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("51.90"));
    }

    @Test
    void createOrder_oneOverStock_throwsException() {
        User user = createUser();
        Product product = createProduct(1L, "OverByOne", new BigDecimal("10.00"), 5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 6, new BigDecimal("10.00"), null)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(InsufficientStockException.class)
                .satisfies(ex -> {
                    InsufficientStockException ise = (InsufficientStockException) ex;
                    assertThat(ise.getAvailableStock()).isEqualTo(5);
                    assertThat(ise.getRequestedQuantity()).isEqualTo(6);
                });
    }

    @Test
    void createOrder_variantExactStock_succeeds() {
        User user = createUser();
        Product product = createProduct(1L, "VP", new BigDecimal("10.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setStock(3);
        variant.setProduct(product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 3, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(variant.getStock()).isEqualTo(0);
        assertThat(result.getLines()).hasSize(1);
    }

    @Test
    void createOrder_variantOneOverStock_throwsException() {
        User user = createUser();
        Product product = createProduct(1L, "VP", new BigDecimal("10.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setStock(3);
        variant.setProduct(product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 4, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        assertThatThrownBy(() -> orderService.createOrder(request, user))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void createOrder_variantWithNoColorNoSize_emptyLabel() {
        User user = createUser();
        Product product = createProduct(1L, "VP", new BigDecimal("10.00"), null);

        ProductVariant variant = new ProductVariant();
        variant.setId(10L);
        variant.setStock(10);
        variant.setProduct(product);
        variant.setColor(null);
        variant.setSize(null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productVariantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 1, new BigDecimal("10.00"), 10L)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines().get(0).getVariantLabel()).isEqualTo("");
    }

    @Test
    void createOrder_lineDetailsCorrect() {
        User user = createUser();
        Product product = createProduct(1L, "LineTest", new BigDecimal("15.00"), 100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderRequest.OrderLineRequest(1L, 4, new BigDecimal("15.00"), null)),
                null, null, null, null, null, null
        );

        CustomerOrder result = orderService.createOrder(request, user);

        assertThat(result.getLines().get(0).getQuantity()).isEqualTo(4);
        assertThat(result.getLines().get(0).getUnitPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(result.getLines().get(0).getProduct()).isSameAs(product);
        assertThat(result.getLines().get(0).getVariantLabel()).isNull();
    }
}
