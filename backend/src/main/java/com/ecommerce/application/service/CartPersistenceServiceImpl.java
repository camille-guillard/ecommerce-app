package com.ecommerce.application.service;

import com.ecommerce.domain.model.Cart;
import com.ecommerce.domain.model.CartItem;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.CartRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.service.CartPersistenceService;
import com.ecommerce.infrastructure.dto.CartSyncRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartPersistenceServiceImpl implements CartPersistenceService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartPersistenceServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart getCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    @Override
    @Transactional
    public Cart syncCart(User user, CartSyncRequest request) {
        Cart cart = getCart(user);
        cart.getItems().clear();

        for (CartSyncRequest.CartSyncItem item : request.items()) {
            Product product = productRepository.findById(item.productId()).orElse(null);
            if (product != null && item.quantity() > 0) {
                cart.getItems().add(new CartItem(cart, product, item.quantity()));
            }
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart mergeCart(User user, CartSyncRequest localItems) {
        Cart cart = getCart(user);

        Map<Long, CartItem> existingByProductId = cart.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), item -> item));

        for (CartSyncRequest.CartSyncItem localItem : localItems.items()) {
            CartItem existing = existingByProductId.get(localItem.productId());
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + localItem.quantity());
            } else {
                Product product = productRepository.findById(localItem.productId()).orElse(null);
                if (product != null && localItem.quantity() > 0) {
                    cart.getItems().add(new CartItem(cart, product, localItem.quantity()));
                }
            }
        }

        return cartRepository.save(cart);
    }
}
