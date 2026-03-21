package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.Cart;
import com.ecommerce.domain.model.CartItem;

import java.util.List;

public record CartResponse(List<CartItemResponse> items) {

    public static CartResponse fromEntity(Cart cart) {
        return new CartResponse(
                cart.getItems().stream().map(CartItemResponse::fromEntity).toList()
        );
    }

    public record CartItemResponse(Long productId, int quantity) {
        public static CartItemResponse fromEntity(CartItem item) {
            return new CartItemResponse(item.getProduct().getId(), item.getQuantity());
        }
    }
}
