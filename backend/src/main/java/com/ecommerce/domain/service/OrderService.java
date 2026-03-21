package com.ecommerce.domain.service;

import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.User;
import com.ecommerce.infrastructure.dto.CreateOrderRequest;

import java.util.List;

public interface OrderService {

    List<CustomerOrder> getOrdersByUser(Long userId);

    CustomerOrder getOrderById(Long id);

    CustomerOrder createOrder(CreateOrderRequest request, User user);
}
