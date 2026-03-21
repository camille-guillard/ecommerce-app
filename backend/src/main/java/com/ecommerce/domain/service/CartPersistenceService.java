package com.ecommerce.domain.service;

import com.ecommerce.domain.model.Cart;
import com.ecommerce.domain.model.User;
import com.ecommerce.infrastructure.dto.CartSyncRequest;

public interface CartPersistenceService {

    Cart getCart(User user);

    Cart syncCart(User user, CartSyncRequest request);

    Cart mergeCart(User user, CartSyncRequest localItems);
}
