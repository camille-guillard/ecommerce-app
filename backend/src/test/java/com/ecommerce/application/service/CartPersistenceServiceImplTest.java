package com.ecommerce.application.service;

import com.ecommerce.application.service.CartPersistenceServiceImpl;
import com.ecommerce.domain.model.Cart;
import com.ecommerce.domain.model.CartItem;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.CartRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.infrastructure.dto.CartSyncRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartPersistenceServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartPersistenceServiceImpl cartPersistenceService;

    private User createUser() {
        User user = new User("testuser", "hash", "test@test.com", "Test", "User", null, null, null);
        user.setId(1L);
        return user;
    }

    private Product createProduct(Long id) {
        Category cat = new Category("cat", "Category");
        cat.setId(1L);
        Product p = new Product("product-" + id, "Product " + id, "desc", new BigDecimal("10.00"), 10, null, cat);
        p.setId(id);
        return p;
    }

    @Test
    void getCart_existingCart_returnsIt() {
        User user = createUser();
        Cart cart = new Cart(user);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        Cart result = cartPersistenceService.getCart(user);

        assertThat(result).isSameAs(cart);
    }

    @Test
    void getCart_noExistingCart_createsNewOne() {
        User user = createUser();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartPersistenceService.getCart(user);

        assertThat(result.getUser()).isEqualTo(user);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void syncCart_replacesAllItemsWithNewOnes() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Product p2 = createProduct(2L);

        Cart cart = new Cart(user);
        cart.getItems().add(new CartItem(cart, createProduct(3L), 5));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 2, null),
                new CartSyncRequest.CartSyncItem(2L, 3, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getProduct().getId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void syncCart_ignoresInvalidProductIds() {
        User user = createUser();
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(999L, 2, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void syncCart_ignoresZeroQuantity() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 0, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void mergeCart_existingProductInCart_addsQuantities() {
        User user = createUser();
        Product p1 = createProduct(1L);

        Cart cart = new Cart(user);
        cart.getItems().add(new CartItem(cart, p1, 3));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 2, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void mergeCart_newProduct_addsToCart() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Product p2 = createProduct(2L);

        Cart cart = new Cart(user);
        cart.getItems().add(new CartItem(cart, p1, 3));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(2L, 4, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    void mergeCart_invalidProductId_skipsIt() {
        User user = createUser();
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(999L, 1, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void mergeCart_newProductWithZeroQuantity_skipsIt() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 0, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).isEmpty();
    }

    // ========== MUTATION-KILLING TESTS ==========

    @Test
    void syncCart_positiveQuantity_addsItem() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        // quantity=1 (boundary: > 0 check)
        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 1, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(1);
        assertThat(result.getItems().get(0).getProduct().getId()).isEqualTo(1L);
    }

    @Test
    void syncCart_negativeQuantity_skipsIt() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, -1, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void mergeCart_existingProduct_quantityIsExactSum() {
        User user = createUser();
        Product p1 = createProduct(1L);

        Cart cart = new Cart(user);
        cart.getItems().add(new CartItem(cart, p1, 7));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 3, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    void mergeCart_newProductPositiveQuantity_addsToCart() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, 1, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void syncCart_clearsThenAddsNewItems() {
        User user = createUser();
        Product oldProduct = createProduct(10L);
        Product newProduct = createProduct(20L);

        Cart cart = new Cart(user);
        cart.getItems().add(new CartItem(cart, oldProduct, 5));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(20L)).thenReturn(Optional.of(newProduct));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest request = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(20L, 2, null)
        ));

        Cart result = cartPersistenceService.syncCart(user, request);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProduct().getId()).isEqualTo(20L);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void getCart_returnsSavedCartForNewUser() {
        User user = createUser();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            return c;
        });

        Cart result = cartPersistenceService.getCart(user);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isSameAs(user);
    }

    @Test
    void mergeCart_negativeQuantityForNewProduct_skipsIt() {
        User user = createUser();
        Product p1 = createProduct(1L);
        Cart cart = new Cart(user);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartSyncRequest localItems = new CartSyncRequest(List.of(
                new CartSyncRequest.CartSyncItem(1L, -1, null)
        ));

        Cart result = cartPersistenceService.mergeCart(user, localItems);

        assertThat(result.getItems()).isEmpty();
    }
}
