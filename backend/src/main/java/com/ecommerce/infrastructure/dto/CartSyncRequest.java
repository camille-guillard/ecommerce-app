package com.ecommerce.infrastructure.dto;

import java.util.List;

public record CartSyncRequest(List<CartSyncItem> items) {

    public record CartSyncItem(Long productId, int quantity, Long variantId) {
    }
}
