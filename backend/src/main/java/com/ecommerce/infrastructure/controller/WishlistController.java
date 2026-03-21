package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.model.WishlistItem;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.WishlistRepository;
import com.ecommerce.infrastructure.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistController(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<?> getWishlist(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(item -> {
                    var map = new HashMap<String, Object>();
                    map.put("productId", item.getProduct().getId());
                    map.put("productDisplayName", item.getProduct().getDisplayName());
                    map.put("productImageUrl", item.getProduct().getImageUrl() != null ? item.getProduct().getImageUrl() : "");
                    map.put("productPrice", item.getProduct().getPrice());
                    map.put("productStock", item.getProduct().getStock());
                    map.put("productAvailable", item.getProduct().isAvailable());
                    return map;
                })
                .toList();
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            return ResponseEntity.ok().build();
        }
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        wishlistRepository.save(new WishlistItem(user, product));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return ResponseEntity.ok().build();
    }
}
