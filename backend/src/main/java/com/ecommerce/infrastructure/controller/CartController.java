package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.Cart;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.service.CartPersistenceService;
import com.ecommerce.infrastructure.dto.CartResponse;
import com.ecommerce.infrastructure.dto.CartSyncRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartPersistenceService cartPersistenceService;

    public CartController(CartPersistenceService cartPersistenceService) {
        this.cartPersistenceService = cartPersistenceService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Cart cart = cartPersistenceService.getCart(user);
        return ResponseEntity.ok(CartResponse.fromEntity(cart));
    }

    @PutMapping
    public ResponseEntity<CartResponse> syncCart(Authentication authentication, @RequestBody CartSyncRequest request) {
        User user = (User) authentication.getPrincipal();
        Cart cart = cartPersistenceService.syncCart(user, request);
        return ResponseEntity.ok(CartResponse.fromEntity(cart));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartResponse> mergeCart(Authentication authentication, @RequestBody CartSyncRequest request) {
        User user = (User) authentication.getPrincipal();
        Cart cart = cartPersistenceService.mergeCart(user, request);
        return ResponseEntity.ok(CartResponse.fromEntity(cart));
    }
}
